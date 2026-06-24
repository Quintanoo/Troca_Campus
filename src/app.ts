import express from "express";
import cors from "cors";
import { routes } from "./routes";
import path from "path";

const app = express();

app.use(cors());
app.use(express.json());

app.use("/uploads", express.static(path.resolve(process.cwd(), "uploads")));

app.get("/", (req, res) => {
  return res.json({
    message: "API TrocaCampus rodando!",
  });
});

app.get("/health", (req, res) => {
  return res.json({
    status: "ok",
    message: "API TrocaCampus funcionando corretamente.",
    timestamp: new Date(),
  });
});

app.use(routes);

export { app };