import { Router } from "express";

import {
  userGet
} from "../controllers";

const router = Router();

router.get("/", (req, res, next) => {
  res.json({ hello: "world" });
});

router.get("/users/:userid", userGet);

export default router;
