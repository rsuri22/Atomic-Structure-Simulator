package com.example.chickennugget;


import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;


public class Linear extends Application {

    public static final float WIDTH = 1400;
    public static final float HEIGHT = 800;
    public static final float RADIUS = 25;
    public static final float CYLHEIGHT = RADIUS * 3;
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    @Override
    public void start(Stage primaryStage) throws Exception {

        final PhongMaterial bondMaterial = new PhongMaterial();
        bondMaterial.setDiffuseColor(Color.DARKGRAY);
        bondMaterial.setSpecularColor(Color.GRAY);

        final PhongMaterial atom1Material = new PhongMaterial();
        atom1Material.setDiffuseColor(Color.DARKRED);
        atom1Material.setSpecularColor(Color.RED);

        final PhongMaterial atom2Material = new PhongMaterial();
        atom2Material.setDiffuseColor(Color.WHITE);
        atom2Material.setSpecularColor(Color.LIGHTBLUE);



        Sphere centralAtom = new Sphere(RADIUS);
        centralAtom.setMaterial(atom1Material);

        Sphere outerAtom1 = new Sphere(RADIUS);
        outerAtom1.translateXProperty().set(CYLHEIGHT);
        outerAtom1.setMaterial(atom2Material);

        Sphere outerAtom2 = new Sphere(RADIUS);
        outerAtom2.translateXProperty().set(-CYLHEIGHT);
        outerAtom2.setMaterial(atom2Material);










        Cylinder bond1 = createConnection((new Point3D(0, 0, 0)), new Point3D(CYLHEIGHT, 0, 0));
        bond1.setMaterial(bondMaterial);

        Cylinder bond2 = createConnection((new Point3D(0, 0, 0)), new Point3D(-CYLHEIGHT, 0, 0));
        bond2.setMaterial(bondMaterial);



        Group group = new Group(centralAtom, outerAtom1, outerAtom2, bond2, bond1);

        Camera camera = new PerspectiveCamera();
        Scene scene = new Scene(group, WIDTH, HEIGHT, true);
        scene.setFill(Color.CORNFLOWERBLUE);
        scene.setCamera(camera);

        group.translateXProperty().set(WIDTH/2);
        group.translateYProperty().set(HEIGHT/2);
        group.translateZProperty().set(-100);

        initMouseControl(group, scene, primaryStage);

        primaryStage.setTitle("Structure Generation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Cylinder createConnection(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(RADIUS/5, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }



    private void initMouseControl(Group group, Scene scene, Stage stage) {
        Rotate xRotate;
        Rotate yRotate;
        group.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );
        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = angleX.get();
            anchorAngleY = angleY.get();
        });

        scene.setOnMouseDragged(event -> {
            angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
            angleY.set(anchorAngleY + (anchorX - event.getSceneX()));
        });

        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            double movement = event.getDeltaY();
            group.translateZProperty().set(group.getTranslateZ() + movement);
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}