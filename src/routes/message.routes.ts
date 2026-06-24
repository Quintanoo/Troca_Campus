import { Router } from "express";
import { prisma } from "../config/prisma";
import { authMiddleware } from "../middlewares/auth.middleware";

const messageRoutes = Router();

messageRoutes.get("/trades/:tradeId/messages", authMiddleware, async (req, res) => {
  const tradeId = String(req.params.tradeId);

  const trade = await prisma.trade.findUnique({
    where: {
      id: tradeId,
    },
    include: {
      product: true,
    },
  });

  if (!trade) {
    return res.status(404).json({
      message: "Negociação não encontrada.",
    });
  }

  const isRequester = trade.requesterId === req.userId;
  const isProductOwner = trade.product.userId === req.userId;

  if (!isRequester && !isProductOwner) {
    return res.status(403).json({
      message: "Você não tem permissão para visualizar as mensagens desta negociação.",
    });
  }

  const messages = await prisma.message.findMany({
    where: {
      tradeId,
    },
    include: {
      sender: {
        select: {
          id: true,
          name: true,
          campus: true,
        },
      },
    },
    orderBy: {
      createdAt: "asc",
    },
  });

  return res.json(messages);
});

messageRoutes.post("/trades/:tradeId/messages", authMiddleware, async (req, res) => {
  const tradeId = String(req.params.tradeId);
  const { text } = req.body;

  if (!text) {
    return res.status(400).json({
      message: "Texto da mensagem é obrigatório.",
    });
  }

  const trade = await prisma.trade.findUnique({
    where: {
      id: tradeId,
    },
    include: {
      product: true,
    },
  });

  if (!trade) {
    return res.status(404).json({
      message: "Negociação não encontrada.",
    });
  }

  const isRequester = trade.requesterId === req.userId;
  const isProductOwner = trade.product.userId === req.userId;

  if (!isRequester && !isProductOwner) {
    return res.status(403).json({
      message: "Você não tem permissão para enviar mensagem nesta negociação.",
    });
  }

  if (trade.status !== "PENDING") {
    return res.status(400).json({
      message: "Só é possível enviar mensagens em negociações pendentes.",
    });
  }

  const message = await prisma.message.create({
    data: {
      tradeId,
      senderId: req.userId as string,
      text,
    },
    include: {
      sender: {
        select: {
          id: true,
          name: true,
          campus: true,
        },
      },
    },
  });

  return res.status(201).json(message);
});

export { messageRoutes };