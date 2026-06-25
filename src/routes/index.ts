import { Router } from "express";
import { categoryRoutes } from "./category.routes";
import { authRoutes } from "./auth.routes";
import { productRoutes } from "./product.routes";
import { tradeRoutes } from "./trade.routes";
import { messageRoutes } from "./message.routes";
import { reviewRoutes } from "./review.routes";
import { userRoutes } from "./user.routes";

const routes = Router();

routes.use("/categories", categoryRoutes);
routes.use("/auth", authRoutes);
routes.use("/products", productRoutes);
routes.use("/trades", tradeRoutes);
routes.use("/users", userRoutes);
routes.use(messageRoutes);
routes.use(reviewRoutes);

export { routes };