import { Router } from "express";
import { categoryRoutes } from "./category.routes";
import { authRoutes } from "./auth.routes";

const routes = Router();

routes.use("/categories", categoryRoutes);
routes.use("/auth", authRoutes);

export { routes };