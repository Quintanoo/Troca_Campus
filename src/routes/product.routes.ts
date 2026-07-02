import { Router } from "express";
import { prisma } from "../config/prisma";
import { authMiddleware } from "../middlewares/auth.middleware";
import { upload } from "../config/upload";

const productRoutes = Router();

// Adicionamos o upload.single("photo") como middleware
productRoutes.post("/", authMiddleware, upload.single("photo"), async (req, res) => {
  const { title, description, categoryId, condition, interests } = req.body;

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

  // 1. Cria o produto normalmente
  const product = await prisma.product.create({
    data: {
      title,
      description,
      categoryId,
      condition,
      interests, // Salva o interesse
      userId: req.userId as string,
    },
  });

  // 2. Se uma foto foi enviada na requisição, salva a URL dela associada ao produto
  if (req.file) {
    const photoUrl = `${req.protocol}://${req.get("host")}/uploads/${req.file.filename}`;

    await prisma.productPhoto.create({
      data: {
        url: photoUrl,
        productId: product.id,
      },
    });
  }

  // 3. Busca o produto novamente para incluir as fotos e retornar ao aplicativo
  const productWithRelations = await prisma.product.findUnique({
    where: {
      id: product.id,
    },
    include: {
      category: true,
      photos: true,
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

  return res.status(201).json(productWithRelations);
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
      photos: true,
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
      photos: true,
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
  const { title, description, categoryId, condition, status, interests } = req.body;

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
      interests,
    },
    include: {
      category: true,
      photos: true,
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