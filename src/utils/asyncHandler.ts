import { NextFunction, Request, Response } from "express";
import multer from "multer";

export function errorMiddleware(
  error: Error,
  req: Request,
  res: Response,
  next: NextFunction
) {
  console.error(error);

  if (error instanceof multer.MulterError) {
    if (error.code === "LIMIT_FILE_SIZE") {
      return res.status(400).json({
        message: "A imagem deve ter no máximo 5MB.",
      });
    }

    return res.status(400).json({
      message: "Erro ao fazer upload da imagem.",
    });
  }

  if (error.message === "Formato de imagem inválido.") {
    return res.status(400).json({
      message: "Formato de imagem inválido. Envie JPG, PNG ou WEBP.",
    });
  }

  return res.status(500).json({
    message: "Erro interno do servidor.",
  });
}