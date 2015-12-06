import { Router } from "express";

import {
  userGet, userAuthCreate, userAuth, userOptionalAuth, userUpdate
} from "../controllers";

const router = Router();

router.get("/", (req, res, next) => {
  res.json({ hello: "world" });
});

// Not in REST scope, needed for authentication
router.post("/auth", userAuthCreate);

router.get("/users/:userid", userOptionalAuth, userGet);
router.put("/users/:userid", userAuth, userUpdate);

export default router;
