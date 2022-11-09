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

public class Octahedral extends Application {
    public static final float WIDTH = 1400;
    public static final float HEIGHT = 800;
    public static final float RADIUS = 25;
    public static final float CYLHEIGHT = RADIUS * 3;
    public static final float SKELTHICKNESS = RADIUS/25;
    public static final Point3D NODE1 = new Point3D(0, CYLHEIGHT, 0);
    public static final Point3D NODE2 = new Point3D(CYLHEIGHT/Math.sqrt(2), 0, CYLHEIGHT/Math.sqrt(2));
    public static final Point3D NODE3 = new Point3D(-CYLHEIGHT/Math.sqrt(2), 0, -CYLHEIGHT/Math.sqrt(2));
    public static final Point3D NODE4 = new Point3D(-CYLHEIGHT/Math.sqrt(2), 0, CYLHEIGHT/Math.sqrt(2));
    public static final Point3D NODE5 = new Point3D(CYLHEIGHT/Math.sqrt(2), 0, -CYLHEIGHT/Math.sqrt(2));
    public static final Point3D NODE6 = new Point3D(0, -CYLHEIGHT, 0);
    public static final Point3D ORIGIN = new Point3D(0, 0, 0);
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

        final PhongMaterial centralAtomMaterial = new PhongMaterial();
        centralAtomMaterial.setDiffuseColor(Color.DARKRED);
        centralAtomMaterial.setSpecularColor(Color.RED);

        final PhongMaterial outerAtomMaterial = new PhongMaterial();
        outerAtomMaterial.setDiffuseColor(Color.WHITE);
        outerAtomMaterial.setSpecularColor(Color.LIGHTBLUE);

        final PhongMaterial skeletonMaterial = new PhongMaterial();
        skeletonMaterial.setDiffuseColor(Color.LIMEGREEN);
        skeletonMaterial.setSpecularColor(Color.CHARTREUSE);

        Sphere outerAtom1 = new Sphere(RADIUS);
        outerAtom1.translateYProperty().set(NODE1.getY());
        outerAtom1.setMaterial(outerAtomMaterial);

        Sphere centralAtom = new Sphere(RADIUS);
        centralAtom.setMaterial(centralAtomMaterial);

        Sphere outerAtom2 = new Sphere(RADIUS);
        outerAtom2.translateXProperty().set(NODE2.getX());
        outerAtom2.translateZProperty().set(NODE2.getZ());
        outerAtom2.setMaterial(outerAtomMaterial);

        Sphere outerAtom3 = new Sphere(RADIUS);
        outerAtom3.translateXProperty().set(NODE3.getX());
        outerAtom3.translateZProperty().set(NODE3.getZ());
        outerAtom3.setMaterial(outerAtomMaterial);

        Sphere outerAtom4 = new Sphere(RADIUS);
        outerAtom4.translateXProperty().set(NODE4.getX());
        outerAtom4.translateZProperty().set(NODE4.getZ());
        outerAtom4.setMaterial(outerAtomMaterial);

        Sphere outerAtom5 = new Sphere(RADIUS);
        outerAtom5.translateXProperty().set(NODE5.getX());
        outerAtom5.translateZProperty().set(NODE5.getZ());
        outerAtom5.setMaterial(outerAtomMaterial);

        Sphere outerAtom6 = new Sphere(RADIUS);
        outerAtom6.translateYProperty().set(NODE6.getY());
        Cylinder bond1 = createConnection(ORIGIN, NODE1);
        bond1.setMaterial(bondMaterial);

        Cylinder bond2 = createConnection(ORIGIN, NODE2);
        bond2.setMaterial(bondMaterial);

        Cylinder bond3 = createConnection(ORIGIN, NODE3);
        bond3.setMaterial(bondMaterial);
        System.out.println(bond3.getHeight());

        Cylinder bond4 = createConnection(ORIGIN, NODE4);
        bond4.setMaterial(bondMaterial);

        Cylinder bond5 = createConnection(ORIGIN, NODE5);
        bond5.setMaterial(bondMaterial);

        Cylinder bond6 = createConnection(ORIGIN, NODE6);
        bond6.setMaterial(bondMaterial);

        Cylinder skeleton1 = createConnection(NODE1, NODE2);
        skeleton1.setRadius(SKELTHICKNESS);
        skeleton1.setMaterial(skeletonMaterial);

        Cylinder skeleton2 = createConnection(NODE1, NODE3);
        skeleton2.setRadius(SKELTHICKNESS);
        skeleton2.setMaterial(skeletonMaterial);

        Cylinder skeleton3 = createConnection(NODE1, NODE4);
        skeleton3.setRadius(SKELTHICKNESS);
        skeleton3.setMaterial(skeletonMaterial);

        Cylinder skeleton4 = createConnection(NODE1, NODE5);
        skeleton4.setRadius(SKELTHICKNESS);
        skeleton4.setMaterial(skeletonMaterial);

        Cylinder skeleton5 = createConnection(NODE6, NODE2);
        skeleton5.setRadius(SKELTHICKNESS);
        skeleton5.setMaterial(skeletonMaterial);

        Cylinder skeleton6 = createConnection(NODE6, NODE3);
        skeleton6.setRadius(SKELTHICKNESS);
        skeleton6.setMaterial(skeletonMaterial);

        Cylinder skeleton7 = createConnection(NODE6, NODE4);
        skeleton7.setRadius(SKELTHICKNESS);
        skeleton7.setMaterial(skeletonMaterial);

        Cylinder skeleton8 = createConnection(NODE6, NODE5);
        skeleton8.setRadius(SKELTHICKNESS);
        skeleton8.setMaterial(skeletonMaterial);

        Cylinder skeleton9 = createConnection(NODE2, NODE5);
        skeleton9.setRadius(SKELTHICKNESS);
        skeleton9.setMaterial(skeletonMaterial);

        Cylinder skeleton10 = createConnection(NODE3, NODE5);
        skeleton10.setRadius(SKELTHICKNESS);
        skeleton10.setMaterial(skeletonMaterial);

        Cylinder skeleton11 = createConnection(NODE4, NODE2);
        skeleton11.setRadius(SKELTHICKNESS);
        skeleton11.setMaterial(skeletonMaterial);

        Cylinder skeleton12 = createConnection(NODE3, NODE4);
        skeleton12.setRadius(SKELTHICKNESS);
        skeleton12.setMaterial(skeletonMaterial);

        Group main = new Group();
        Group atoms = new Group(centralAtom, outerAtom1, outerAtom2, outerAtom3, outerAtom4, outerAtom5, outerAtom6);
        Group bonds = new Group (bond1, bond2, bond3, bond4, bond5, bond6);
        Group skeleton = new Group(skeleton1, skeleton2, skeleton3, skeleton4, skeleton5, skeleton6, skeleton7, skeleton8, skeleton9, skeleton10, skeleton11, skeleton12);
        main.getChildren().add(atoms);
        main.getChildren().add(bonds);
        main.getChildren().add(skeleton);

        Camera camera = new PerspectiveCamera();
        Scene scene = new Scene(main, WIDTH, HEIGHT, true);
        scene.setFill(Color.CORNFLOWERBLUE);
        scene.setCamera(camera);

        main.translateXProperty().set(WIDTH/2);
        main.translateYProperty().set(HEIGHT/2);
        main.translateZProperty().set(-100);

        initMouseControl(main, scene, primaryStage);
        handleKeyboard(atoms, bonds, skeleton, scene);

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

    private void handleKeyboard(Group atoms, Group bonds, Group skeleton, Scene scene){
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Q:
                    atoms.setVisible(!atoms.isVisible());
                    break;
                case W:
                    bonds.setVisible(!bonds.isVisible());
                    break;
                case E:
                    skeleton.setVisible(!skeleton.isVisible());
                    break;


            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}