import { Router } from "express";
import { prisma } from "../config/prisma";
import { authMiddleware } from "../middlewares/auth.middleware";

const tradeRoutes = Router();

tradeRoutes.post("/", authMiddleware, async (req, res) => {
  const { productId } = req.body;

  if (!productId) {
    return res.status(400).json({
      message: "Produto é obrigatório.",
    });
  }

  const product = await prisma.product.findUnique({
    where: {
      id: productId,
    },
  });

  if (!product) {
    return res.status(404).json({
      message: "Produto não encontrado.",
    });
  }

  if (product.status !== "ACTIVE") {
    return res.status(400).json({
      message: "Este produto não está disponível para troca.",
    });
  }

  if (product.userId === req.userId) {
    return res.status(400).json({
      message: "Você não pode propor troca no seu próprio produto.",
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
      requesterId: req.userId as string,
    },
    include: {
      product: {
        include: {
          category: true,
          photos: true,
          user: {
            select: {
              id: true,
              name: true,
              campus: true,
              phone: true,
              reputation: true,
            },
          },
        },
      },
      requester: {
        select: {
          id: true,
          name: true,
          campus: true,
          phone: true,
          reputation: true,
        },
      },
    },
  });

  return res.status(201).json(trade);
});

tradeRoutes.get("/my", authMiddleware, async (req, res) => {
  const trades = await prisma.trade.findMany({
    where: {
      OR: [
        {
          requesterId: req.userId,
        },
        {
          product: {
            userId: req.userId,
          },
        },
      ],
    },
    include: {
      product: {
        include: {
          category: true,
          photos: true,
          user: {
            select: {
              id: true,
              name: true,
              campus: true,
              phone: true,
              reputation: true,
            },
          },
        },
      },
      requester: {
        select: {
          id: true,
          name: true,
          campus: true,
          phone: true,
          reputation: true,
        },
      },
    },
    orderBy: {
      createdAt: "desc",
    },
  });

  return res.json(trades);
});

tradeRoutes.get("/:id", authMiddleware, async (req, res) => {
  const id = String(req.params.id);

  const trade = await prisma.trade.findUnique({
    where: {
      id,
    },
    include: {
      product: {
        include: {
          category: true,
          photos: true,
          user: {
            select: {
              id: true,
              name: true,
              campus: true,
              phone: true,
              reputation: true,
            },
          },
        },
      },
      requester: {
        select: {
          id: true,
          name: true,
          campus: true,
          phone: true,
          reputation: true,
        },
      },
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
      message: "Você não tem permissão para visualizar esta negociação.",
    });
  }

  return res.json(trade);
});

tradeRoutes.patch("/:id/accept", authMiddleware, async (req, res) => {
  const id = String(req.params.id);

  const trade = await prisma.trade.findUnique({
    where: {
      id,
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

  if (trade.product.userId !== req.userId) {
    return res.status(403).json({
      message: "Apenas o dono do produto pode aceitar a proposta.",
    });
  }

  if (trade.status !== "PENDING") {
    return res.status(400).json({
      message: "Esta proposta não está pendente.",
    });
  }

  const updatedTrade = await prisma.$transaction(async (tx) => {
    const acceptedTrade = await tx.trade.update({
      where: {
        id,
      },
      data: {
        status: "ACCEPTED",
      },
      include: {
        product: {
          include: {
            category: true,
            photos: true,
          },
        },
        requester: {
          select: {
            id: true,
            name: true,
            campus: true,
            phone: true,
            reputation: true,
          },
        },
      },
    });

    await tx.product.update({
      where: {
        id: trade.productId,
      },
      data: {
        status: "TRADED",
      },
    });

    await tx.trade.updateMany({
      where: {
        productId: trade.productId,
        id: {
          not: id,
        },
        status: "PENDING",
      },
      data: {
        status: "CANCELED",
      },
    });

    return acceptedTrade;
  });

  return res.json(updatedTrade);
});

tradeRoutes.patch("/:id/cancel", authMiddleware, async (req, res) => {
  const id = String(req.params.id);

  const trade = await prisma.trade.findUnique({
    where: {
      id,
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
      message: "Você não tem permissão para cancelar esta negociação.",
    });
  }

  if (trade.status !== "PENDING") {
    return res.status(400).json({
      message: "Apenas propostas pendentes podem ser canceladas.",
    });
  }

  const updatedTrade = await prisma.trade.update({
    where: {
      id,
    },
    data: {
      status: "CANCELED",
    },
    include: {
      product: {
        include: {
          category: true,
          photos: true,
        },
      },
      requester: {
        select: {
          id: true,
          name: true,
          campus: true,
          phone: true,
          reputation: true,
        },
      },
    },
  });

  return res.json(updatedTrade);
});

export { tradeRoutes };