import { PrismaClient } from "@prisma/client";

const prisma = new PrismaClient();

async function main() {
  const categories = [
    "Livros",
    "Eletrônicos",
    "Materiais de Estudo",
    "Roupas",
    "Móveis",
    "Outros",
  ];

  for (const name of categories) {
    await prisma.category.upsert({
      where: {
        name,
      },
      update: {},
      create: {
        name,
      },
    });
  }

  console.log("Categorias iniciais cadastradas com sucesso.");
}

main()
  .catch((error) => {
    console.error("Erro ao executar seed:", error);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });