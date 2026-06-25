import multer from "multer";
import path from "path";
import fs from "fs";

const uploadFolder = path.resolve(process.cwd(), "uploads", "products");

if (!fs.existsSync(uploadFolder)) {
  fs.mkdirSync(uploadFolder, { recursive: true });
}

const storage = multer.diskStorage({
  destination: uploadFolder,

  filename: (req, file, callback) => {
    const fileExtension = path.extname(file.originalname);
    const fileName = `${Date.now()}-${Math.round(Math.random() * 1e9)}${fileExtension}`;

    callback(null, fileName);
  },
});

const fileFilter: multer.Options["fileFilter"] = (req, file, callback) => {
  const allowedTypes = ["image/jpeg", "image/jpg", "image/png", "image/webp"];

  if (!allowedTypes.includes(file.mimetype)) {
    return callback(new Error("Formato de imagem inválido."));
  }

  callback(null, true);
};

const upload = multer({
  storage,
  fileFilter,
  limits: {
    fileSize: 5 * 1024 * 1024,
  },
});

export { upload };