import { Router } from "express";
import { prisma } from "../config/prisma";
import { authMiddleware } from "../middlewares/auth.middleware";

const reviewRoutes = Router();

reviewRoutes.post("/trades/:tradeId/reviews", authMiddleware, async (req, res) => {
  const tradeId = String(req.params.tradeId);
  const { rating, comment } = req.body;

  if (!rating) {
    return res.status(400).json({
      message: "Nota é obrigatória.",
    });
  }

  if (rating < 1 || rating > 5) {
    return res.status(400).json({
      message: "A nota deve ser entre 1 e 5.",
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

  if (trade.status !== "ACCEPTED") {
    return res.status(400).json({
      message: "Só é possível avaliar uma troca aceita.",
    });
  }

  const isRequester = trade.requesterId === req.userId;
  const isProductOwner = trade.product.userId === req.userId;

  if (!isRequester && !isProductOwner) {
    return res.status(403).json({
      message: "Você não tem permissão para avaliar esta troca.",
    });
  }

  const targetId = isRequester ? trade.product.userId : trade.requesterId;

  const existingReview = await prisma.review.findFirst({
    where: {
      tradeId,
      authorId: req.userId,
    },
  });

  if (existingReview) {
    return res.status(400).json({
      message: "Você já avaliou esta troca.",
    });
  }

  const review = await prisma.$transaction(async (tx) => {
    const createdReview = await tx.review.create({
      data: {
        tradeId,
        authorId: req.userId as string,
        targetId,
        rating,
        comment,
      },
      include: {
        author: {
          select: {
            id: true,
            name: true,
            campus: true,
          },
        },
        target: {
          select: {
            id: true,
            name: true,
            campus: true,
            reputation: true,
          },
        },
      },
    });

    const reviewsReceived = await tx.review.findMany({
      where: {
        targetId,
      },
      select: {
        rating: true,
      },
    });

    const reputation =
      reviewsReceived.reduce((sum, item) => sum + item.rating, 0) /
      reviewsReceived.length;

    await tx.user.update({
      where: {
        id: targetId,
      },
      data: {
        reputation,
      },
    });

    return createdReview;
  });

  return res.status(201).json(review);
});

reviewRoutes.get("/users/:userId/reviews", async (req, res) => {
  const userId = String(req.params.userId);

  const user = await prisma.user.findUnique({
    where: {
      id: userId,
    },
    select: {
      id: true,
      name: true,
      campus: true,
      reputation: true,
    },
  });

  if (!user) {
    return res.status(404).json({
      message: "Usuário não encontrado.",
    });
  }

  const reviews = await prisma.review.findMany({
    where: {
      targetId: userId,
    },
    include: {
      author: {
        select: {
          id: true,
          name: true,
          campus: true,
        },
      },
      trade: {
        include: {
          product: {
            select: {
              id: true,
              title: true,
            },
          },
        },
      },
    },
    orderBy: {
      createdAt: "desc",
    },
  });

  return res.json({
    user,
    reviews,
  });
});

export { reviewRoutes };