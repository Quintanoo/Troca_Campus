import { Router } from "express";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import { prisma } from "../config/prisma";
import { authMiddleware } from "../middlewares/auth.middleware";

const authRoutes = Router();

authRoutes.post("/register", async (req, res) => {
  const { name, email, password, campus, phone } = req.body;

  if (!name || !email || !password) {
    return res.status(400).json({
      message: "Nome, e-mail e senha são obrigatórios.",
    });
  }

  const emailAlreadyExists = await prisma.user.findUnique({
    where: {
      email,
    },
  });

  if (emailAlreadyExists) {
    return res.status(400).json({
      message: "Este e-mail já está cadastrado.",
    });
  }

  const passwordHash = await bcrypt.hash(password, 10);

  const user = await prisma.user.create({
    data: {
      name,
      email,
      passwordHash,
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
      createdAt: true,
    },
  });

  return res.status(201).json(user);
});

authRoutes.post("/login", async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({
      message: "E-mail e senha são obrigatórios.",
    });
  }

  const user = await prisma.user.findUnique({
    where: {
      email,
    },
  });

  if (!user) {
    return res.status(401).json({
      message: "E-mail ou senha inválidos.",
    });
  }

  const passwordMatches = await bcrypt.compare(password, user.passwordHash);

  if (!passwordMatches) {
    return res.status(401).json({
      message: "E-mail ou senha inválidos.",
    });
  }

  const token = jwt.sign(
    {
      userId: user.id,
    },
    process.env.JWT_SECRET as string,
    {
      expiresIn: "7d",
    }
  );

  return res.json({
    user: {
      id: user.id,
      name: user.name,
      email: user.email,
      campus: user.campus,
      phone: user.phone,
      reputation: user.reputation,
      status: user.status,
    },
    token,
  });
});

authRoutes.get("/me", authMiddleware, async (req, res) => {
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
    },
  });

  if (!user) {
    return res.status(404).json({
      message: "Usuário não encontrado.",
    });
  }

  return res.json(user);
});

export { authRoutes };