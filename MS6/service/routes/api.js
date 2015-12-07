import { Router } from "express";

import {
  userGet, userAuthCreate, userAuth, userOptionalAuth, userUpdate,
  userCreate, userDelete
} from "../controllers";

const router = Router();

router.get("/", (req, res, next) => {
  res.json({ hello: "world" });
});

// Not in REST scope, needed for authentication
router.post("/auth", userAuthCreate);

// User Routes
router.post("/users", userCreate);
router.get("/users/:userid", userOptionalAuth, userGet);
router.put("/users/:userid", userAuth, userUpdate);
router.delete("/users/:userid", userAuth, userDelete);

export default router;
