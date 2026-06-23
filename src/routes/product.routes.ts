import { Router } from "express";
import { prisma } from "../config/prisma";
import { authMiddleware } from "../middlewares/auth.middleware";

const productRoutes = Router();

productRoutes.post("/", authMiddleware, async (req, res) => {
  const { title, description, categoryId, condition } = req.body;

  if (!title || !description || !categoryId || !condition) {
    return res.status(400).json({
      message: "Título, descrição, categoria e condição são obrigatórios.",
    });
  }

  const category = await prisma.category.findUnique({
    where: {
      id: categoryId,
    },
  });

  if (!category) {
    return res.status(404).json({
      message: "Categoria não encontrada.",
    });
  }

  const product = await prisma.product.create({
    data: {
      title,
      description,
      categoryId,
      condition,
      userId: req.userId as string,
    },
    include: {
      category: true,
      user: {
        select: {
          id: true,
          name: true,
          campus: true,
          reputation: true,
        },
      },
    },
  });

  return res.status(201).json(product);
});

productRoutes.get("/", async (req, res) => {
  const { search, categoryId, condition, campus } = req.query;

  const products = await prisma.product.findMany({
    where: {
      status: "ACTIVE",

      title: search
        ? {
            contains: String(search),
            mode: "insensitive",
          }
        : undefined,

      categoryId: categoryId ? String(categoryId) : undefined,

      condition: condition ? (String(condition) as any) : undefined,

      user: campus
        ? {
            campus: {
              contains: String(campus),
              mode: "insensitive",
            },
          }
        : undefined,
    },
    include: {
      category: true,
      user: {
        select: {
          id: true,
          name: true,
          campus: true,
          reputation: true,
        },
      },
    },
    orderBy: {
      createdAt: "desc",
    },
  });

  return res.json(products);
});

productRoutes.get("/my", authMiddleware, async (req, res) => {
  const products = await prisma.product.findMany({
    where: {
      userId: req.userId,
    },
    include: {
      category: true,
    },
    orderBy: {
      createdAt: "desc",
    },
  });

  return res.json(products);
});

productRoutes.get("/:id", async (req, res) => {
  const id = String(req.params.id);

  const product = await prisma.product.findUnique({
    where: {
      id,
    },
    include: {
      category: true,
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
  });

  if (!product) {
    return res.status(404).json({
      message: "Produto não encontrado.",
    });
  }

  return res.json(product);
});

productRoutes.put("/:id", authMiddleware, async (req, res) => {
  const id = String(req.params.id);
  const { title, description, categoryId, condition, status } = req.body;

  const product = await prisma.product.findUnique({
    where: {
      id,
    },
  });

  if (!product) {
    return res.status(404).json({
      message: "Produto não encontrado.",
    });
  }

  if (product.userId !== req.userId) {
    return res.status(403).json({
      message: "Você não tem permissão para editar este produto.",
    });
  }

  const updatedProduct = await prisma.product.update({
    where: {
      id,
    },
    data: {
      title,
      description,
      categoryId,
      condition,
      status,
    },
    include: {
      category: true,
    },
  });

  return res.json(updatedProduct);
});

productRoutes.delete("/:id", authMiddleware, async (req, res) => {
  const id = String(req.params.id);

  const product = await prisma.product.findUnique({
    where: {
      id,
    },
  });

  if (!product) {
    return res.status(404).json({
      message: "Produto não encontrado.",
    });
  }

  if (product.userId !== req.userId) {
    return res.status(403).json({
      message: "Você não tem permissão para excluir este produto.",
    });
  }

  await prisma.product.delete({
    where: {
      id,
    },
  });

  return res.status(204).send();
});

export { productRoutes };