package modules.userprofile;

import com.sun.javafx.tk.Toolkit;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    Logger log = LoggerFactory.getLogger(Graph.class);

    private static int x0 = 10;
    private static int y0 = 10;
    private static int width = 150;
    private static int height = 50;
    private static int offset = 10;

    private static int heightPlus = 10;


    private List<Vertex> nodes = new ArrayList<Vertex>();
    private List<Edge<Vertex>> edges = new ArrayList<Edge<Vertex>>();


    public void addNode(Vertex node){
        nodes.add(node);
    }

    public void addNodes(List<Vertex> nodes){
        for (Vertex node : nodes) {
            addNode(node);
        }
    }

    public void addEdge(Edge<Vertex> edge){

        Vertex f = null;
        Vertex t = null;
        for (Vertex node : nodes) {
            if(node.equals(edge.getFrom())){
                f = node;
            }
            if(node.equals(edge.getTo())){
                t = node;
            }
        }
        if(f==null || t == null){
            throw new RuntimeException("Node not found in graph");
        }
        f.addFrom();
        t.addTo();

        edges.add(new Edge<Vertex>(f,t));
    }

    public void addEdge(Vertex from, Vertex to){
        Edge<Vertex> edge = new Edge<Vertex>(from, to);
        addEdge(edge);
    }

    public void addEdges(List<Edge<Vertex>> edges){
        for (Edge<Vertex> edge : edges) {
            addEdge(edge);
        }
    }

    public Canvas generate(){
        Canvas canvas = new Canvas(500, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        int y = y0;
        for (Vertex node : nodes) {
            node = computeHeight(node);
            drawVertex(gc,node,x0,y);
            node.setX(x0);
            node.setY(y);
            y = y + node.getHeight() + offset;
        }

        int marginLeft = -20;
        int margin = 20;
        int step = 1;
        for (Edge<Vertex> edge : edges) {
            log.info(edge.getFrom().toString());
            if(edge.getFrom().isLeft()){
                int startX = edge.getFrom().getX() ;
                int startY = edge.getFrom().getY() + (edge.getFrom().getDrawLefts()+1) * offset;

                int _x = startX + marginLeft;
                marginLeft = marginLeft - 20;

                int endX = edge.getTo().getX();
                int endY = edge.getTo().getY() + (edge.getTo().getDrawRights()+1) * offset;

                gc.setFont(new Font(10));
                gc.fillText(String.valueOf(step),startX,startY+3);
                gc.strokeLine(startX, startY, _x, startY);
                gc.strokeLine(_x, startY, _x, endY);
                gc.strokeLine(_x, endY, endX, endY);
                gc.setFont(new Font(20));
                gc.fillText(">",endX-8,endY+6);


                edge.getTo().setLeft(true);
                edge.getTo().drawn();
                edge.getFrom().drawn();
                edge.getFrom().setLeft(false);
            }else{
                int startX = edge.getFrom().getX() + width;
                int startY = edge.getFrom().getY() + (edge.getFrom().getDrawRights()+1) * offset;

                int _x = startX + margin;
                margin = margin + 20;

                int endX = edge.getTo().getX() + width;
                int endY = edge.getTo().getY() + (edge.getTo().getDrawRights()+1) * offset;

                gc.setFont(new Font(10));
                gc.fillText(String.valueOf(step),startX-10,startY+3);
                gc.strokeLine(startX, startY, _x, startY);
                gc.strokeLine(_x, startY, _x, endY);
                gc.strokeLine(_x, endY, endX, endY);
                gc.setFont(new Font(20));
                gc.fillText("<",endX-7,endY+6);

                edge.getTo().setLeft(false);
                edge.getTo().drawn();
                edge.getFrom().drawn();
                edge.getFrom().setLeft(true);
            }
            step++;
        }

        return canvas;
    }

    private Vertex computeHeight(Vertex node){
        int m = Math.max(node.getFrom(),node.getTo());
        int h = m * heightPlus + height;
        node.setHeight(h);
        return node;
    }

    private void drawVertex(GraphicsContext gc, Vertex node, double x, double y){
        Font font = gc.getFont();

        float sWidth = Toolkit.getToolkit().getFontLoader().computeStringWidth(node.getName(), font);
        float sHeight = Toolkit.getToolkit().getFontLoader().getFontMetrics(font).getLineHeight();

        float textXPos = new Double(x).intValue() + (width / 2) - sWidth / 2;
        float textYPos = new Double(y).intValue() + (height / 2);

        gc.setFill(Color.GREEN);
        gc.fillRect(x, y, width, node.getHeight());
        gc.setFill(Color.BLACK);

        gc.fillText(node.getName(), textXPos, textYPos);
    }

}
