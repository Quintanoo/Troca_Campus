import { Router } from "express";
import { categoryRoutes } from "./category.routes";
import { authRoutes } from "./auth.routes";
import { productRoutes } from "./product.routes";
import { tradeRoutes } from "./trade.routes";
import { messageRoutes } from "./message.routes";
import { reviewRoutes } from "./review.routes";

const routes = Router();

routes.use("/categories", categoryRoutes);
routes.use("/auth", authRoutes);
routes.use("/products", productRoutes);
routes.use("/trades", tradeRoutes);
routes.use(messageRoutes);
routes.use(reviewRoutes);

export { routes };