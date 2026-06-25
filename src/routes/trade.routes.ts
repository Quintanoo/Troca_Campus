import { Router } from "express";
import { prisma } from "../config/prisma";
import { authMiddleware } from "../middlewares/auth.middleware";

const tradeRoutes = Router();

tradeRoutes.post("/", authMiddleware, async (req, res) => {
  const { productId, offeredProductId } = req.body;

  if (!productId || !offeredProductId) {
    return res.status(400).json({
      message: "Produto desejado e produto oferecido são obrigatórios.",
    });
  }

  // Verifica o produto que a pessoa QUER
  const product = await prisma.product.findUnique({
    where: { id: productId },
  });

  if (!product || product.status !== "ACTIVE") {
    return res.status(400).json({
      message: "Este produto não está disponível para troca.",
    });
  }

  if (product.userId === req.userId) {
    return res.status(400).json({
      message: "Você não pode propor troca no seu próprio produto.",
    });
  }

  // Verifica o produto que a pessoa ESTÁ OFERECENDO
  const offeredProduct = await prisma.product.findUnique({
    where: { id: offeredProductId },
  });

  if (!offeredProduct || offeredProduct.userId !== req.userId || offeredProduct.status !== "ACTIVE") {
    return res.status(400).json({
      message: "O produto oferecido não é válido ou não pertence a você.",
    });
  }

  const existingTrade = await prisma.trade.findFirst({
    where: {
      productId,
      requesterId: req.userId,
      status: "PENDING",
    },
  });

  if (existingTrade) {
    return res.status(400).json({
      message: "Você já possui uma proposta pendente para este produto.",
    });
  }

  const trade = await prisma.trade.create({
    data: {
      productId,
      offeredProductId,
      requesterId: req.userId as string,
    },
    include: {
      product: { include: { category: true, photos: true } },
      offeredProduct: { include: { category: true, photos: true } },
    },
  });

  return res.status(201).json(trade);
});

export { tradeRoutes };