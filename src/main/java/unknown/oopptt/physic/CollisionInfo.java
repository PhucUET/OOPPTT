package unknown.oopptt.physic;

public class CollisionInfo {
    public double normalX, normalY;  // pháp tuyến (hướng đẩy ra)
    public double penetration;       // độ chồng lấn
    public double contactX, contactY;// điểm tiếp xúc (ước lượng)
    public String side;              // mặt va chạm ("top"|"bottom"|"left"|"right"|"overlap")

    public CollisionInfo(double nx, double ny, double pen, String side,
                         double cx, double cy) {
        this.normalX = nx;
        this.normalY = ny;
        this.penetration = pen;
        this.side = side;
        this.contactX = cx;
        this.contactY = cy;
    }
}





