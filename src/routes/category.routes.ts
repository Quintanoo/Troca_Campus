import { Router } from "express";
import { prisma } from "../config/prisma";

const categoryRoutes = Router();

categoryRoutes.post("/", async (req, res) => {
  const { name } = req.body;

  const category = await prisma.category.create({
    data: {
      name,
    },
  });

  return res.status(201).json(category);
});

categoryRoutes.get("/", async (req, res) => {
  const categories = await prisma.category.findMany({
    orderBy: {
      name: "asc",
    },
  });

  return res.json(categories);
});

export { categoryRoutes };