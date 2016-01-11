import { Router } from "express";

import {
  userGet, userAuthCreate, userAuth, userOptionalAuth, userUpdate,
  userCreate, userDelete, entryCreate, entryGet, entryDelete, entryUpdate,
  courseList, courseGet, verificationCreate, verificationGet
} from "../controllers";

const router = Router();

// Not in REST scope, needed for authentication
router.post("/auth", userAuthCreate);


// Rest API
// ---
// User Routes
router.post("/users", userCreate);
router.get("/users/:userid", userOptionalAuth, userGet);
router.put("/users/:userid", userAuth, userUpdate);
router.delete("/users/:userid", userAuth, userDelete);

// Entry Routes
router.post("/entries", userAuth, entryCreate);
router.get("/entries/:entryid", entryGet);
router.put("/entries/:entryid", userAuth, entryUpdate);
router.delete("/entries/:entryid", userAuth, entryDelete);

// Course Routes
router.get("/courses", courseList);
router.get("/courses/:courseid", courseGet);

// Verification Routes
router.post("/verification", userAuth, verificationCreate);
router.get("/verification/:code", userAuth, verificationGet);

// TLD Routes
// router.get("/tlds", tldList);


export default router;
