import { Router } from "express";

import {
  userGet
} from "../controllers";

const router = Router();

router.get("/", (req, res, next) => {
  res.json({ hello: "world" });
});

router.get("/users/:id", userGet);

export default router;
