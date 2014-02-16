package modules.userprofile;

public class Vertex {

    private String name;
    private int to;
    private int from;
    private int height;
    private int x;
    private int y;

    private boolean left = false;
    private int drawLefts;
    private int drawRights;

    public Vertex(String name) {
        this.name = name;
        drawLefts = 0;
        drawRights = 0;
    }

    public void addTo(){
        this.to++;
    }

    public void addFrom(){
        this.from++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        if (!name.equals(vertex.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void drawn(){
        if(left) drawLefts++;
        else drawRights++;
    }

    public int getDrawLefts() {
        return drawLefts;
    }

    public int getDrawRights() {
        return drawRights;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "name='" + name + '\'' +
                ", to=" + to +
                ", from=" + from +
                ", height=" + height +
                ", x=" + x +
                ", y=" + y +
                ", left=" + left +
                ", drawn=[" + drawLefts + ","+drawRights+"]"+
                '}';
    }
}
