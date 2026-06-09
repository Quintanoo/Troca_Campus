import express from "express";
import cors from "cors";
import { routes } from "./routes";

const app = express();

app.use(cors());
app.use(express.json());

app.get("/", (req, res) => {
  return res.json({
    message: "API TrocaCampus rodando!",
  });
});

app.use(routes);

export { app };