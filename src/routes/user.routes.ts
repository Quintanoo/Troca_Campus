import { Router } from "express";
import { prisma } from "../config/prisma";
import { authMiddleware } from "../middlewares/auth.middleware";

const userRoutes = Router();

userRoutes.get("/me", authMiddleware, async (req, res) => {
  const user = await prisma.user.findUnique({
    where: {
      id: req.userId,
    },
    select: {
      id: true,
      name: true,
      email: true,
      campus: true,
      phone: true,
      reputation: true,
      status: true,
      createdAt: true,
      updatedAt: true,
    },
  });

  if (!user) {
    return res.status(404).json({
      message: "Usuário não encontrado.",
    });
  }

  return res.json(user);
});

userRoutes.put("/me", authMiddleware, async (req, res) => {
  const { name, campus, phone } = req.body;

  const user = await prisma.user.update({
    where: {
      id: req.userId,
    },
    data: {
      name,
      campus,
      phone,
    },
    select: {
      id: true,
      name: true,
      email: true,
      campus: true,
      phone: true,
      reputation: true,
      status: true,
      updatedAt: true,
    },
  });

  return res.json(user);
});

userRoutes.get("/me/history", authMiddleware, async (req, res) => {
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
      reviews: {
        include: {
          author: {
            select: {
              id: true,
              name: true,
            },
          },
          target: {
            select: {
              id: true,
              name: true,
            },
          },
        },
      },
    },
    orderBy: {
      createdAt: "desc",
    },
  });

  return res.json(trades);
});

userRoutes.get("/:id", async (req, res) => {
  const id = String(req.params.id);

  const user = await prisma.user.findUnique({
    where: {
      id,
    },
    select: {
      id: true,
      name: true,
      campus: true,
      reputation: true,
      createdAt: true,
      products: {
        where: {
          status: "ACTIVE",
        },
        include: {
          category: true,
          photos: true,
        },
        orderBy: {
          createdAt: "desc",
        },
      },
      reviewsReceived: {
        include: {
          author: {
            select: {
              id: true,
              name: true,
              campus: true,
            },
          },
        },
        orderBy: {
          createdAt: "desc",
        },
      },
    },
  });

  if (!user) {
    return res.status(404).json({
      message: "Usuário não encontrado.",
    });
  }

  return res.json(user);
});

export { userRoutes };