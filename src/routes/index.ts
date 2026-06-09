import { Router } from "express";
import { categoryRoutes } from "./category.routes";

const routes = Router();

routes.use("/categories", categoryRoutes);

export { routes };