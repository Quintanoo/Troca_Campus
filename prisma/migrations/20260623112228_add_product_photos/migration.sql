-- CreateTable
CREATE TABLE "product_photos" (
    "id" TEXT NOT NULL,
    "productId" TEXT NOT NULL,
    "url" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "product_photos_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "product_photos" ADD CONSTRAINT "product_photos_productId_fkey" FOREIGN KEY ("productId") REFERENCES "products"("id") ON DELETE CASCADE ON UPDATE CASCADE;
