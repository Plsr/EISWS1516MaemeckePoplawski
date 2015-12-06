import { Router } from "express";

import {
  userGet, userAuthCreate, userAuth
} from "../controllers";

const router = Router();

router.get("/", (req, res, next) => {
  res.json({ hello: "world" });
});

// Not in REST scope, needed for authentication
router.post("/auth", userAuthCreate);

router.get("/users/:userid", userGet);

export default router;
