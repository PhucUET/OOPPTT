package unknown.oopptt.physic;

import unknown.oopptt.api.GameEntity;


public class CollisionSystem {

    public static CollisionInfo intersectAABB(GameEntity a, GameEntity b) {

        double ax = a.x, ay = a.y, aw = a.w, ah = a.h;
        double bx = b.x, by = b.y, bw = b.w, bh = b.h;


        double dx = (ax + aw / 2.0) - (bx + bw / 2.0);
        double dy = (ay + ah / 2.0) - (by + bh / 2.0);
        double px = (aw / 2.0 + bw / 2.0) - Math.abs(dx);
        double py = (ah / 2.0 + bh / 2.0) - Math.abs(dy);


        if (px <= 0 || py <= 0) return null;


        if (px < py) {
            double nx = dx > 0 ? 1 : -1;
            String side = (nx > 0) ? "left" : "right";
            double cx = (dx > 0) ? ax : ax + aw;
            double cy = ay + ah / 2.0;
            return new CollisionInfo(nx, 0, px, side, cx, cy);
        } else {
            double ny = dy > 0 ? 1 : -1;
            String side = (ny > 0) ? "top" : "bottom";
            double cx = ax + aw / 2.0;
            double cy = (dy > 0) ? ay : ay + ah;
            return new CollisionInfo(0, ny, py, side, cx, cy);
        }
    }


    public static void resolveCollision(GameEntity a, GameEntity b, CollisionInfo info) {
// Tách tối thiểu entity di động (ưu tiên Ball)
        if ("ball".equals(a.kind)) {
            a.x += info.normalX * info.penetration;
            a.y += info.normalY * info.penetration;
        } else if ("ball".equals(b.kind)) {
            b.x -= info.normalX * info.penetration;
            b.y -= info.normalY * info.penetration;
        } else {
// nếu cả hai tĩnh/không phải bóng: đẩy a và b ngược chiều
            a.x += info.normalX * info.penetration * 0.5;
            a.y += info.normalY * info.penetration * 0.5;
            b.x -= info.normalX * info.penetration * 0.5;
            b.y -= info.normalY * info.penetration * 0.5;
        }

// Gọi callback hai phía
        a.onCollision(b, info);
        CollisionInfo infoB = new CollisionInfo(-info.normalX, -info.normalY, info.penetration,
                opposite(info.side), info.contactX, info.contactY);
        b.onCollision(a, infoB);
    }


    private static String opposite(String side) {
        return switch (side) {
            case "left" -> "right";
            case "right" -> "left";
            case "top" -> "bottom";
            case "bottom" -> "top";
            default -> "overlap";
        };
    }
}
