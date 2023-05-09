package com.example.chickennugget;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.*;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
public class StructureBuilder {
    public static final float WIDTH = 1400;
    public static final float HEIGHT = 800;
    private static double MOUSE_SPEED = 0.1;
    private static double ROTATION_SPEED = 6;
    public static double DEFAULTRADIUS = 25;
    public static double outerRadius = DEFAULTRADIUS;
    public static double centralRadius = DEFAULTRADIUS;
    public static double SKELTHICKNESS = DEFAULTRADIUS /25;
    public static double CYLHEIGHT = centralRadius * 3;
    public static double TETANGLE = 0.22675;
    public static final Point3D ORIGIN = new Point3D(0, 0, 0);
    public static final Point3D TET_ORIGIN = new Point3D(0, -6.1, 0);
    public static final Point3D TRIGPY_ORIGIN = new Point3D(0, -15, 0);
    public static final Point3D NODE_1 = new Point3D(0, CYLHEIGHT, 0);
    public static final Point3D NODE_2 = new Point3D(0, -CYLHEIGHT, 0);
    public static final Point3D NODE_3 = new Point3D(-CYLHEIGHT / 2, 0, CYLHEIGHT * (Math.sqrt(3) / 2));
    public static final Point3D NODE_4 = new Point3D(-CYLHEIGHT / 2, 0, -CYLHEIGHT * (Math.sqrt(3) / 2));
    public static final Point3D NODE_5 = new Point3D(CYLHEIGHT, 0, 0);
    public static Point3D NODE_6 = new Point3D(-CYLHEIGHT * Math.sqrt(3) / 4, CYLHEIGHT * Math.sin(TETANGLE), CYLHEIGHT * 3 / 4);
    public static Point3D NODE_7 = new Point3D(-CYLHEIGHT * Math.sqrt(3) / 4, CYLHEIGHT * Math.sin(TETANGLE), -CYLHEIGHT * 3 / 4);
    public static Point3D NODE_8 = new Point3D((CYLHEIGHT * (Math.sqrt(3) / 2)), CYLHEIGHT * Math.sin(TETANGLE), 0);
    public static final Point3D NODE_9 = new Point3D(CYLHEIGHT/Math.sqrt(2), 0, CYLHEIGHT/Math.sqrt(2));
    public static final Point3D NODE_10 = new Point3D(-CYLHEIGHT/Math.sqrt(2), 0, -CYLHEIGHT/Math.sqrt(2));
    public static final Point3D NODE_11 = new Point3D(-CYLHEIGHT/Math.sqrt(2), 0, CYLHEIGHT/Math.sqrt(2));
    public static final Point3D NODE_12 = new Point3D(CYLHEIGHT/Math.sqrt(2), 0, -CYLHEIGHT/Math.sqrt(2));
    public static final Point3D NODE_13 = new Point3D(-CYLHEIGHT, 0, 0);

    public static final Point3D NODE_14 = new Point3D(-CYLHEIGHT, 0, -CYLHEIGHT*Math.cos(0.9119));
    public static final Point3D NODE_15 = new Point3D(CYLHEIGHT, 0, -CYLHEIGHT*Math.cos(0.9119));
    private static double anchorX, anchorY;
    private static double anchorAngleX = 0;
    private static double anchorAngleY = 0;
    private static final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private static final DoubleProperty angleY = new SimpleDoubleProperty(0);
    private static double mouseStartPosX, mouseStartPosY;
    private static double mousePosX, mousePosY;
    private static double mouseOldX, mouseOldY;
    private static double mouseDeltaX, mouseDeltaY;
    public static Color defaultOuterAtomColor = Color.DARKRED;
    public static Color defaultCentralAtomColor = Color.WHITE;
    public static Color outerColor = defaultOuterAtomColor;
    public static Color centralColor = defaultCentralAtomColor;
    public static void addRotation(Group group, double angle, Point3D axis) {
        Rotate r = new Rotate(angle, axis);
        /**
         * This is the important bit and thanks to bronkowitz in this post
         * https://stackoverflow.com/questions/31382634/javafx-3d-rotations for
         * getting me to the solution that the rotations need accumulated in
         * this way
         */
        group.getTransforms().set(0, r.createConcatenation(group.getTransforms().get(0)));
    }
    public static Cylinder createConnection(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(DEFAULTRADIUS/5, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }
    private static void initMouseControl(Group group, Scene scene, Stage stage) {
        scene.setOnMousePressed(me -> {
            mouseStartPosX = me.getSceneX();
            mouseStartPosY = me.getSceneY();
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = me.getSceneX();
            mouseDeltaY = me.getSceneY();
        });

        scene.setOnMouseDragged(me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);
            group.getTransforms().add(new Affine());
            addRotation(group, -mouseDeltaX* MOUSE_SPEED * ROTATION_SPEED, Rotate.Y_AXIS);
            addRotation(group, mouseDeltaY* MOUSE_SPEED * ROTATION_SPEED, Rotate.X_AXIS);
        });

        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            double movement = event.getDeltaY();
            group.translateZProperty().set(group.getTranslateZ() + movement);
        });

    }
    private static void handleKeyboard(Group atoms, Group bonds, Group skeleton, Scene scene){
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
    public static void setCentralRadius(double r){
        centralRadius = r;
    }
    public static void setOuterRadius(double r){
        outerRadius = r;
    }
    public static void setOuterColor(Color c) {
        outerColor = c;
    }
    public static void setCentralColor(Color c) {
        centralColor = c;
    }
    public static Sphere createAtom(Point3D node, PhongMaterial material, double radius) {
        Sphere a = new Sphere(radius);
        a.translateXProperty().set(node.getX());
        a.translateYProperty().set(node.getY());
        a.translateZProperty().set(node.getZ());
        a.setMaterial(material);
        return a;
    }
    public static Cylinder createBond(Point3D node1, Point3D node2, PhongMaterial material){
        Cylinder c = createConnection(node1, node2);
        c.setMaterial(material);
        return c;
    }
    public static Cylinder createSkeleton(Point3D node1, Point3D node2, PhongMaterial material){
        Cylinder s = createConnection(node1, node2);
        s.setRadius(DEFAULTRADIUS/25);
        s.setMaterial(material);
        return s;
    }
    public static class Octahedral extends Application{
        public void start(Stage primaryStage) throws Exception{

            final PhongMaterial bondMaterial = new PhongMaterial();
            bondMaterial.setDiffuseColor(Color.DARKGRAY);
            bondMaterial.setSpecularColor(Color.GRAY);

            final PhongMaterial centralAtomMaterial = new PhongMaterial();
            centralAtomMaterial.setDiffuseColor(centralColor);
            centralAtomMaterial.setSpecularColor(centralColor);

            final PhongMaterial outerAtomMaterial = new PhongMaterial();
            outerAtomMaterial.setDiffuseColor(outerColor);
            outerAtomMaterial.setSpecularColor(outerColor);

            final PhongMaterial skeletonMaterial = new PhongMaterial();
            skeletonMaterial.setDiffuseColor(Color.LIMEGREEN);
            skeletonMaterial.setSpecularColor(Color.CHARTREUSE);

            Sphere outerAtom1 = createAtom(NODE_1, outerAtomMaterial, outerRadius);
            Sphere centralAtom = createAtom(ORIGIN, centralAtomMaterial, centralRadius);
            Sphere outerAtom2 = createAtom(NODE_2, outerAtomMaterial, outerRadius);
            Sphere outerAtom3 = createAtom(NODE_9, outerAtomMaterial, outerRadius);
            Sphere outerAtom4 = createAtom(NODE_10, outerAtomMaterial, outerRadius);
            Sphere outerAtom5 = createAtom(NODE_11, outerAtomMaterial, outerRadius);
            Sphere outerAtom6 = createAtom(NODE_12, outerAtomMaterial, outerRadius);

            Cylinder bond1 = createBond(ORIGIN, NODE_1, bondMaterial);
            Cylinder bond2 = createBond(ORIGIN, NODE_2, bondMaterial);
            Cylinder bond3 = createBond(ORIGIN, NODE_9, bondMaterial);
            Cylinder bond4 = createBond(ORIGIN, NODE_10, bondMaterial);
            Cylinder bond5 = createBond(ORIGIN, NODE_11, bondMaterial);
            Cylinder bond6 = createBond(ORIGIN, NODE_12, bondMaterial);

            Cylinder skeleton1 = createSkeleton(NODE_1, NODE_9, skeletonMaterial);
            Cylinder skeleton2 = createSkeleton(NODE_1, NODE_10, skeletonMaterial);
            Cylinder skeleton3 = createSkeleton(NODE_1, NODE_11, skeletonMaterial);
            Cylinder skeleton4 = createSkeleton(NODE_1, NODE_12, skeletonMaterial);
            Cylinder skeleton5 = createSkeleton(NODE_2, NODE_9, skeletonMaterial);
            Cylinder skeleton6 = createSkeleton(NODE_2, NODE_10, skeletonMaterial);
            Cylinder skeleton7 = createSkeleton(NODE_2, NODE_11, skeletonMaterial);
            Cylinder skeleton8 = createSkeleton(NODE_2, NODE_12, skeletonMaterial);
            Cylinder skeleton9 = createSkeleton(NODE_9, NODE_12, skeletonMaterial);
            Cylinder skeleton10 = createSkeleton(NODE_10, NODE_12, skeletonMaterial);
            Cylinder skeleton11 = createSkeleton(NODE_11, NODE_9, skeletonMaterial);
            Cylinder skeleton12 = createSkeleton(NODE_10, NODE_11, skeletonMaterial);

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
    }
    public static class SquarePlanar extends Application{
        public void start(Stage primaryStage) throws Exception{

            final PhongMaterial bondMaterial = new PhongMaterial();
            bondMaterial.setDiffuseColor(Color.DARKGRAY);
            bondMaterial.setSpecularColor(Color.GRAY);

            final PhongMaterial centralAtomMaterial = new PhongMaterial();
            centralAtomMaterial.setDiffuseColor(centralColor);
            centralAtomMaterial.setSpecularColor(centralColor);

            final PhongMaterial outerAtomMaterial = new PhongMaterial();
            outerAtomMaterial.setDiffuseColor(outerColor);
            outerAtomMaterial.setSpecularColor(outerColor);

            final PhongMaterial skeletonMaterial = new PhongMaterial();
            skeletonMaterial.setDiffuseColor(Color.LIMEGREEN);
            skeletonMaterial.setSpecularColor(Color.CHARTREUSE);

            Sphere outerAtom1 = createAtom(NODE_9, outerAtomMaterial, outerRadius);
            Sphere centralAtom = createAtom(ORIGIN, centralAtomMaterial, centralRadius);
            Sphere outerAtom2 = createAtom(NODE_10, outerAtomMaterial, outerRadius);
            Sphere outerAtom3 = createAtom(NODE_11, outerAtomMaterial, outerRadius);
            Sphere outerAtom4 = createAtom(NODE_12, outerAtomMaterial, outerRadius);

            Cylinder bond1 = createBond(ORIGIN, NODE_9, bondMaterial);
            Cylinder bond2 = createBond(ORIGIN, NODE_10, bondMaterial);
            Cylinder bond3 = createBond(ORIGIN, NODE_11, bondMaterial);
            Cylinder bond4 = createBond(ORIGIN, NODE_12, bondMaterial);

            Cylinder skeleton1 = createSkeleton(NODE_9, NODE_10, skeletonMaterial);
            Cylinder skeleton2 = createSkeleton(NODE_10, NODE_11, skeletonMaterial);
            Cylinder skeleton3 = createSkeleton(NODE_9, NODE_12, skeletonMaterial);
            Cylinder skeleton4 = createSkeleton(NODE_9, NODE_11, skeletonMaterial);
            Cylinder skeleton5 = createSkeleton(NODE_10, NODE_12, skeletonMaterial);
            Cylinder skeleton6 = createSkeleton(NODE_11, NODE_12, skeletonMaterial);

            Group main = new Group();
            Group atoms = new Group(centralAtom,outerAtom1, outerAtom2, outerAtom3, outerAtom4);
            Group bonds = new Group (bond1, bond2, bond3, bond4);
            Group skeleton = new Group(skeleton1, skeleton2, skeleton3, skeleton4, skeleton5, skeleton6);
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
    }
    public static class Tetrahedral extends Application {
        public void start(Stage primaryStage) throws Exception {

            final PhongMaterial bondMaterial = new PhongMaterial();
            bondMaterial.setDiffuseColor(Color.DARKGRAY);
            bondMaterial.setSpecularColor(Color.GRAY);

            final PhongMaterial centralAtomMaterial = new PhongMaterial();
            centralAtomMaterial.setDiffuseColor(centralColor);
            centralAtomMaterial.setSpecularColor(centralColor);

            final PhongMaterial outerAtomMaterial = new PhongMaterial();
            outerAtomMaterial.setDiffuseColor(outerColor);
            outerAtomMaterial.setSpecularColor(outerColor);

            final PhongMaterial skeletonMaterial = new PhongMaterial();
            skeletonMaterial.setDiffuseColor(Color.LIMEGREEN);
            skeletonMaterial.setSpecularColor(Color.CHARTREUSE);

            Sphere centralAtom = createAtom(TET_ORIGIN, centralAtomMaterial, centralRadius);
            Sphere outerAtom1 = createAtom(NODE_2, outerAtomMaterial, outerRadius);
            Sphere outerAtom2 = createAtom(NODE_8, outerAtomMaterial, outerRadius);
            Sphere outerAtom3 = createAtom(NODE_7, outerAtomMaterial, outerRadius);
            Sphere outerAtom4 = createAtom(NODE_6, outerAtomMaterial, outerRadius);

            Cylinder bond1 = createBond(TET_ORIGIN, NODE_2, bondMaterial);
            Cylinder bond2 = createBond(TET_ORIGIN, NODE_8, bondMaterial);
            Cylinder bond3 = createBond(TET_ORIGIN, NODE_7, bondMaterial);
            Cylinder bond4 = createBond(TET_ORIGIN, NODE_6, bondMaterial);

            Cylinder skeleton1 = createSkeleton(NODE_2, NODE_8, skeletonMaterial);
            Cylinder skeleton2 = createSkeleton(NODE_2, NODE_7, skeletonMaterial);
            Cylinder skeleton3 = createSkeleton(NODE_2, NODE_6, skeletonMaterial);
            Cylinder skeleton4 = createSkeleton(NODE_7, NODE_8, skeletonMaterial);
            Cylinder skeleton5 = createSkeleton(NODE_7, NODE_6, skeletonMaterial);
            Cylinder skeleton6 = createSkeleton(NODE_6, NODE_8, skeletonMaterial);

            Group main = new Group();
            Group atoms = new Group(centralAtom, outerAtom1, outerAtom2, outerAtom3, outerAtom4);
            Group bonds = new Group(bond1, bond2, bond3, bond4);
            Group skeleton = new Group(skeleton1, skeleton2, skeleton3, skeleton4, skeleton5, skeleton6);
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
    }
    public static class TrigPyramidal extends Application {
        public void start(Stage primaryStage) throws Exception {

            final PhongMaterial bondMaterial = new PhongMaterial();
            bondMaterial.setDiffuseColor(Color.DARKGRAY);
            bondMaterial.setSpecularColor(Color.GRAY);

            final PhongMaterial centralAtomMaterial = new PhongMaterial();
            centralAtomMaterial.setDiffuseColor(centralColor);
            centralAtomMaterial.setSpecularColor(centralColor);

            final PhongMaterial outerAtomMaterial = new PhongMaterial();
            outerAtomMaterial.setDiffuseColor(outerColor);
            outerAtomMaterial.setSpecularColor(outerColor);

            final PhongMaterial skeletonMaterial = new PhongMaterial();
            skeletonMaterial.setDiffuseColor(Color.LIMEGREEN);
            skeletonMaterial.setSpecularColor(Color.CHARTREUSE);

            Sphere centralAtom = createAtom(TRIGPY_ORIGIN, centralAtomMaterial, centralRadius);
            Sphere outerAtom1 = createAtom(NODE_8, outerAtomMaterial, outerRadius);
            Sphere outerAtom2 = createAtom(NODE_7, outerAtomMaterial, outerRadius);
            Sphere outerAtom3 = createAtom(NODE_6, outerAtomMaterial, outerRadius);

            Cylinder bond1 = createBond(TRIGPY_ORIGIN, NODE_8, bondMaterial);
            Cylinder bond2 = createBond(TRIGPY_ORIGIN, NODE_7, bondMaterial);
            Cylinder bond3 = createBond(TRIGPY_ORIGIN, NODE_6, bondMaterial);

            Cylinder skeleton1 = createSkeleton(TRIGPY_ORIGIN, NODE_8, skeletonMaterial);
            Cylinder skeleton2 = createSkeleton(TRIGPY_ORIGIN, NODE_7, skeletonMaterial);
            Cylinder skeleton3 = createSkeleton(TRIGPY_ORIGIN, NODE_6, skeletonMaterial);
            Cylinder skeleton4 = createSkeleton(NODE_7, NODE_8, skeletonMaterial);
            Cylinder skeleton5 = createSkeleton(NODE_7, NODE_6, skeletonMaterial);
            Cylinder skeleton6 = createSkeleton(NODE_6, NODE_8, skeletonMaterial);

            Group main = new Group();
            Group atoms = new Group(centralAtom, outerAtom1, outerAtom2, outerAtom3);
            Group bonds = new Group(bond1, bond2, bond3);
            Group skeleton = new Group(skeleton1, skeleton2, skeleton3, skeleton4, skeleton5, skeleton6);
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
    }
    public static class TrigPlanar extends Application {
        public void start(Stage primaryStage) throws Exception {

            final PhongMaterial bondMaterial = new PhongMaterial();
            bondMaterial.setDiffuseColor(Color.DARKGRAY);
            bondMaterial.setSpecularColor(Color.GRAY);

            final PhongMaterial centralAtomMaterial = new PhongMaterial();
            centralAtomMaterial.setDiffuseColor(centralColor);
            centralAtomMaterial.setSpecularColor(centralColor);

            final PhongMaterial outerAtomMaterial = new PhongMaterial();
            outerAtomMaterial.setDiffuseColor(outerColor);
            outerAtomMaterial.setSpecularColor(outerColor);

            final PhongMaterial skeletonMaterial = new PhongMaterial();
            skeletonMaterial.setDiffuseColor(Color.LIMEGREEN);
            skeletonMaterial.setSpecularColor(Color.CHARTREUSE);

            Sphere centralAtom = new Sphere(centralRadius);
            centralAtom.setMaterial(centralAtomMaterial);

            Sphere outerAtom1 = new Sphere(outerRadius);
            outerAtom1.translateXProperty().set(NODE_5.getX());
            outerAtom1.setMaterial(outerAtomMaterial);

            Sphere outerAtom2 = new Sphere(outerRadius);
            outerAtom2.translateXProperty().set(NODE_4.getX());
            outerAtom2.translateZProperty().set(NODE_4.getZ());
            outerAtom2.setMaterial(outerAtomMaterial);

            Sphere outerAtom3 = new Sphere(outerRadius);
            outerAtom3.translateXProperty().set(NODE_3.getX());
            outerAtom3.translateZProperty().set(NODE_3.getZ());
            outerAtom3.setMaterial(outerAtomMaterial);

            Cylinder bond2 = createConnection(ORIGIN, NODE_5);
            bond2.setMaterial(bondMaterial);

            Cylinder bond3 = createConnection(ORIGIN, NODE_4);
            bond3.setMaterial(bondMaterial);

            Cylinder bond4 = createConnection(ORIGIN, NODE_3);
            bond4.setMaterial(bondMaterial);

            Cylinder skeleton1 = createConnection(ORIGIN, NODE_5);
            skeleton1.setRadius(SKELTHICKNESS);
            skeleton1.setMaterial(skeletonMaterial);

            Cylinder skeleton2 = createConnection(ORIGIN, NODE_4);
            skeleton2.setRadius(SKELTHICKNESS);
            skeleton2.setMaterial(skeletonMaterial);


            Cylinder skeleton3 = createConnection(NODE_5, NODE_3);
            skeleton3.setRadius(SKELTHICKNESS);
            skeleton3.setMaterial(skeletonMaterial);

            Cylinder skeleton4 = createConnection(NODE_4, NODE_5);
            skeleton4.setRadius(SKELTHICKNESS);
            skeleton4.setMaterial(skeletonMaterial);

            Cylinder skeleton5 = createConnection(NODE_3, NODE_4);
            skeleton5.setRadius(SKELTHICKNESS);
            skeleton5.setMaterial(skeletonMaterial);

            Cylinder skeleton6 = createConnection(ORIGIN, NODE_3);
            skeleton6.setRadius(SKELTHICKNESS);
            skeleton6.setMaterial(skeletonMaterial);

            Group main = new Group();
            Group atoms = new Group(centralAtom, outerAtom1, outerAtom2, outerAtom3);
            Group bonds = new Group (bond2, bond3, bond4);
            Group skeleton = new Group(skeleton1, skeleton2, skeleton3, skeleton4, skeleton5, skeleton6);
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
    }
    public static class TrigBipyramidal extends Application {
        public void start(Stage primaryStage) throws Exception {

            final PhongMaterial bondMaterial = new PhongMaterial();
            bondMaterial.setDiffuseColor(Color.DARKGRAY);
            bondMaterial.setSpecularColor(Color.GRAY);

            final PhongMaterial centralAtomMaterial = new PhongMaterial();
            centralAtomMaterial.setDiffuseColor(centralColor);
            centralAtomMaterial.setSpecularColor(centralColor);

            final PhongMaterial outerAtomMaterial = new PhongMaterial();
            outerAtomMaterial.setDiffuseColor(outerColor);
            outerAtomMaterial.setSpecularColor(outerColor);

            final PhongMaterial skeletonMaterial = new PhongMaterial();
            skeletonMaterial.setDiffuseColor(Color.LIMEGREEN);
            skeletonMaterial.setSpecularColor(Color.CHARTREUSE);

            Sphere outerAtom1 = new Sphere(outerRadius);
            outerAtom1.translateYProperty().set(NODE_1.getY());
            outerAtom1.setMaterial(outerAtomMaterial);

            Sphere centralAtom = new Sphere(centralRadius);
            centralAtom.setMaterial(centralAtomMaterial);

            Sphere outerAtom2 = new Sphere(outerRadius);
            outerAtom2.translateXProperty().set(NODE_5.getX());
            outerAtom2.setMaterial(outerAtomMaterial);

            Sphere outerAtom3 = new Sphere(outerRadius);
            outerAtom3.translateXProperty().set(NODE_4.getX());
            outerAtom3.translateZProperty().set(NODE_4.getZ());
            outerAtom3.setMaterial(outerAtomMaterial);

            Sphere outerAtom4 = new Sphere(outerRadius);
            outerAtom4.translateXProperty().set(NODE_3.getX());
            outerAtom4.translateZProperty().set(NODE_3.getZ());
            outerAtom4.setMaterial(outerAtomMaterial);

            Sphere outerAtom5 = new Sphere(outerRadius);
            outerAtom5.translateYProperty().set(NODE_2.getY());
            outerAtom5.setMaterial(outerAtomMaterial);

            Cylinder bond1 = createConnection(ORIGIN, NODE_1);
            bond1.setMaterial(bondMaterial);

            Cylinder bond2 = createConnection(ORIGIN, NODE_5);
            bond2.setMaterial(bondMaterial);

            Cylinder bond3 = createConnection(ORIGIN, NODE_4);
            bond3.setMaterial(bondMaterial);

            Cylinder bond4 = createConnection(ORIGIN, NODE_3);
            bond4.setMaterial(bondMaterial);

            Cylinder bond5 = createConnection(ORIGIN, NODE_2);
            bond5.setMaterial(bondMaterial);

            Cylinder skeleton1 = createConnection(NODE_1, NODE_5);
            skeleton1.setRadius(SKELTHICKNESS);
            skeleton1.setMaterial(skeletonMaterial);

            Cylinder skeleton2 = createConnection(NODE_1, NODE_4);
            skeleton2.setRadius(SKELTHICKNESS);
            skeleton2.setMaterial(skeletonMaterial);

            Cylinder skeleton3 = createConnection(NODE_1, NODE_3);
            skeleton3.setRadius(SKELTHICKNESS);
            skeleton3.setMaterial(skeletonMaterial);

            Cylinder skeleton4 = createConnection(NODE_2, NODE_3);
            skeleton4.setRadius(SKELTHICKNESS);
            skeleton4.setMaterial(skeletonMaterial);

            Cylinder skeleton5 = createConnection(NODE_2, NODE_4);
            skeleton5.setRadius(SKELTHICKNESS);
            skeleton5.setMaterial(skeletonMaterial);

            Cylinder skeleton6 = createConnection(NODE_2, NODE_5);
            skeleton6.setRadius(SKELTHICKNESS);
            skeleton6.setMaterial(skeletonMaterial);

            Cylinder skeleton7 = createConnection(NODE_4, NODE_3);
            skeleton7.setRadius(SKELTHICKNESS);
            skeleton7.setMaterial(skeletonMaterial);

            Cylinder skeleton8 = createConnection(NODE_5, NODE_4);
            skeleton8.setRadius(SKELTHICKNESS);
            skeleton8.setMaterial(skeletonMaterial);

            Cylinder skeleton9 = createConnection(NODE_5, NODE_3);
            skeleton9.setRadius(SKELTHICKNESS);
            skeleton9.setMaterial(skeletonMaterial);

            Group main = new Group();
            Group atoms = new Group(centralAtom, outerAtom1, outerAtom2, outerAtom3, outerAtom4, outerAtom5);
            Group bonds = new Group (bond1, bond2, bond3, bond4, bond5);
            Group skeleton = new Group(skeleton1, skeleton2, skeleton3, skeleton4, skeleton5, skeleton6, skeleton7, skeleton8, skeleton9);
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
    }
    public static class Seesaw extends Application {
        public void start(Stage primaryStage) throws Exception {

            final PhongMaterial bondMaterial = new PhongMaterial();
            bondMaterial.setDiffuseColor(Color.DARKGRAY);
            bondMaterial.setSpecularColor(Color.GRAY);

            final PhongMaterial centralAtomMaterial = new PhongMaterial();
            centralAtomMaterial.setDiffuseColor(centralColor);
            centralAtomMaterial.setSpecularColor(centralColor);

            final PhongMaterial outerAtomMaterial = new PhongMaterial();
            outerAtomMaterial.setDiffuseColor(outerColor);
            outerAtomMaterial.setSpecularColor(outerColor);

            final PhongMaterial skeletonMaterial = new PhongMaterial();
            skeletonMaterial.setDiffuseColor(Color.LIMEGREEN);
            skeletonMaterial.setSpecularColor(Color.CHARTREUSE);

            Sphere outerAtom1 = new Sphere(outerRadius);
            outerAtom1.translateYProperty().set(NODE_1.getY());
            outerAtom1.setMaterial(outerAtomMaterial);

            Sphere centralAtom = new Sphere(centralRadius);
            centralAtom.setMaterial(centralAtomMaterial);


            Sphere outerAtom3 = new Sphere(outerRadius);
            outerAtom3.translateXProperty().set(NODE_4.getX());
            outerAtom3.translateZProperty().set(NODE_4.getZ());
            outerAtom3.setMaterial(outerAtomMaterial);

            Sphere outerAtom4 = new Sphere(outerRadius);
            outerAtom4.translateXProperty().set(NODE_3.getX());
            outerAtom4.translateZProperty().set(NODE_3.getZ());
            outerAtom4.setMaterial(outerAtomMaterial);

            Sphere outerAtom5 = new Sphere(outerRadius);
            outerAtom5.translateYProperty().set(NODE_2.getY());
            outerAtom5.setMaterial(outerAtomMaterial);

            Cylinder bond1 = createConnection(ORIGIN, NODE_1);
            bond1.setMaterial(bondMaterial);

            Cylinder bond3 = createConnection(ORIGIN, NODE_4);
            bond3.setMaterial(bondMaterial);

            Cylinder bond4 = createConnection(ORIGIN, NODE_3);
            bond4.setMaterial(bondMaterial);

            Cylinder bond5 = createConnection(ORIGIN, NODE_2);
            bond5.setMaterial(bondMaterial);

            Cylinder skeleton1 = createConnection(NODE_1, NODE_2);
            skeleton1.setRadius(SKELTHICKNESS);
            skeleton1.setMaterial(skeletonMaterial);

            Cylinder skeleton2 = createConnection(NODE_1, NODE_4);
            skeleton2.setRadius(SKELTHICKNESS);
            skeleton2.setMaterial(skeletonMaterial);

            Cylinder skeleton3 = createConnection(NODE_1, NODE_3);
            skeleton3.setRadius(SKELTHICKNESS);
            skeleton3.setMaterial(skeletonMaterial);

            Cylinder skeleton4 = createConnection(NODE_2, NODE_3);
            skeleton4.setRadius(SKELTHICKNESS);
            skeleton4.setMaterial(skeletonMaterial);

            Cylinder skeleton5 = createConnection(NODE_2, NODE_4);
            skeleton5.setRadius(SKELTHICKNESS);
            skeleton5.setMaterial(skeletonMaterial);

            Cylinder skeleton7 = createConnection(NODE_4, NODE_3);
            skeleton7.setRadius(SKELTHICKNESS);
            skeleton7.setMaterial(skeletonMaterial);


            Group main = new Group();
            Group atoms = new Group(centralAtom, outerAtom1, outerAtom3, outerAtom4, outerAtom5);
            Group bonds = new Group (bond1, bond3, bond4, bond5);
            Group skeleton = new Group(skeleton2, skeleton3, skeleton4, skeleton5, skeleton7);
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
    }
    public static class Linear extends Application {
        public void start(Stage primaryStage) throws Exception {
            final PhongMaterial bondMaterial = new PhongMaterial();
            bondMaterial.setDiffuseColor(Color.DARKGRAY);
            bondMaterial.setSpecularColor(Color.GRAY);

            final PhongMaterial centralAtomMaterial = new PhongMaterial();
            centralAtomMaterial.setDiffuseColor(centralColor);
            centralAtomMaterial.setSpecularColor(centralColor);

            final PhongMaterial outerAtomMaterial = new PhongMaterial();
            outerAtomMaterial.setDiffuseColor(outerColor);
            outerAtomMaterial.setSpecularColor(outerColor);

            final PhongMaterial skeletonMaterial = new PhongMaterial();
            skeletonMaterial.setDiffuseColor(Color.LIMEGREEN);

            Sphere centerAtom = new Sphere(centralRadius);
            centerAtom.setMaterial(centralAtomMaterial);

            Sphere outerAtom1 = new Sphere(outerRadius);
            outerAtom1.translateXProperty().set(NODE_5.getX());
            outerAtom1.setMaterial(outerAtomMaterial);

            Sphere outerAtom2 = new Sphere(outerRadius);
            outerAtom2.translateXProperty().set(NODE_13.getX());
            outerAtom2.setMaterial(outerAtomMaterial);

            Cylinder bond1 = createConnection(ORIGIN, NODE_5);
            bond1.setMaterial(bondMaterial);

            Cylinder bond2 = createConnection(ORIGIN, NODE_13);
            bond2.setMaterial(bondMaterial);

            Cylinder skeleton1 = createConnection(NODE_5, NODE_13);
            skeleton1.setMaterial(skeletonMaterial);
            skeleton1.setRadius(DEFAULTRADIUS/25);

            Group main = new Group();
            Group atoms = new Group(centerAtom, outerAtom1, outerAtom2);
            Group bonds = new Group(bond1, bond2);
            Group skeleton = new Group(skeleton1);
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
    }
    public static class Bent extends Application {
        public void start(Stage primaryStage) throws Exception {
            final PhongMaterial bondMaterial = new PhongMaterial();
            bondMaterial.setDiffuseColor(Color.DARKGRAY);
            bondMaterial.setSpecularColor(Color.GRAY);

            final PhongMaterial centralAtomMaterial = new PhongMaterial();
            centralAtomMaterial.setDiffuseColor(centralColor);
            centralAtomMaterial.setSpecularColor(centralColor);

            final PhongMaterial outerAtomMaterial = new PhongMaterial();
            outerAtomMaterial.setDiffuseColor(outerColor);
            outerAtomMaterial.setSpecularColor(outerColor);

            final PhongMaterial skeletonMaterial = new PhongMaterial();
            skeletonMaterial.setDiffuseColor(Color.LIMEGREEN);

            Sphere centerAtom = createAtom(ORIGIN, centralAtomMaterial, centralRadius);
            Sphere outerAtom1 = createAtom(NODE_15, outerAtomMaterial, outerRadius);
            Sphere outerAtom2 = createAtom(NODE_14, outerAtomMaterial, outerRadius);

            Cylinder bond1 = createBond(ORIGIN, NODE_14, bondMaterial);
            Cylinder bond2 = createBond(ORIGIN, NODE_15, bondMaterial);

            Cylinder skeleton1 = createSkeleton(NODE_14, NODE_15, skeletonMaterial);
            Cylinder skeleton2 = createSkeleton(ORIGIN, NODE_14, skeletonMaterial);
            Cylinder skeleton3 = createSkeleton(ORIGIN, NODE_15, skeletonMaterial);

            Group main = new Group();
            Group atoms = new Group(centerAtom, outerAtom1, outerAtom2);
            Group bonds = new Group(bond1, bond2);
            Group skeleton = new Group(skeleton1, skeleton2, skeleton3);
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
    }
    public static class NodeMap extends Application {
        public void start(Stage primaryStage) throws Exception{
            final PhongMaterial dotMaterial = new PhongMaterial();
            dotMaterial.setDiffuseColor(Color.SILVER);
            dotMaterial.setSpecularColor(Color.GOLD);

            Sphere dot1 = new Sphere(5);
            dot1.translateXProperty().set(NODE_1.getX());
            dot1.translateYProperty().set(NODE_1.getY());
            dot1.translateZProperty().set(NODE_1.getZ());
            dot1.setMaterial(dotMaterial);

            Sphere dot2 = new Sphere(5);
            dot2.translateXProperty().set(NODE_2.getX());
            dot2.translateYProperty().set(NODE_2.getY());
            dot2.translateZProperty().set(NODE_2.getZ());
            dot2.setMaterial(dotMaterial);

            Sphere dot3 = new Sphere(5);
            dot3.translateXProperty().set(NODE_3.getX());
            dot3.translateYProperty().set(NODE_3.getY());
            dot3.translateZProperty().set(NODE_3.getZ());
            dot3.setMaterial(dotMaterial);

            Sphere dot4 = new Sphere(5);
            dot4.translateXProperty().set(NODE_4.getX());
            dot4.translateYProperty().set(NODE_4.getY());
            dot4.translateZProperty().set(NODE_4.getZ());
            dot4.setMaterial(dotMaterial);

            Sphere dot5 = new Sphere(5);
            dot5.translateXProperty().set(NODE_5.getX());
            dot5.translateYProperty().set(NODE_5.getY());
            dot5.translateZProperty().set(NODE_5.getZ());
            dot5.setMaterial(dotMaterial);

            Sphere dot6 = new Sphere(5);
            dot6.translateXProperty().set(NODE_6.getX());
            dot6.translateYProperty().set(NODE_6.getY());
            dot6.translateZProperty().set(NODE_6.getZ());
            dot6.setMaterial(dotMaterial);

            Sphere dot7 = new Sphere(5);
            dot7.translateXProperty().set(NODE_7.getX());
            dot7.translateYProperty().set(NODE_7.getY());
            dot7.translateZProperty().set(NODE_7.getZ());
            dot7.setMaterial(dotMaterial);

            Sphere dot8 = new Sphere(5);
            dot8.translateXProperty().set(NODE_8.getX());
            dot8.translateYProperty().set(NODE_8.getY());
            dot8.translateZProperty().set(NODE_8.getZ());
            dot8.setMaterial(dotMaterial);

            Sphere dot9 = new Sphere(5);
            dot9.translateXProperty().set(NODE_9.getX());
            dot9.translateYProperty().set(NODE_9.getY());
            dot9.translateZProperty().set(NODE_9.getZ());
            dot9.setMaterial(dotMaterial);

            Sphere dot10 = new Sphere(5);
            dot10.translateXProperty().set(NODE_10.getX());
            dot10.translateYProperty().set(NODE_10.getY());
            dot10.translateZProperty().set(NODE_10.getZ());
            dot10.setMaterial(dotMaterial);

            Sphere dot11 = new Sphere(5);
            dot11.translateXProperty().set(NODE_11.getX());
            dot11.translateYProperty().set(NODE_11.getY());
            dot11.translateZProperty().set(NODE_11.getZ());
            dot11.setMaterial(dotMaterial);

            Sphere dot12 = new Sphere(5);
            dot12.translateXProperty().set(NODE_12.getX());
            dot12.translateYProperty().set(NODE_12.getY());
            dot12.translateZProperty().set(NODE_12.getZ());
            dot12.setMaterial(dotMaterial);

            Group main = new Group();
            Group atoms = new Group(dot1, dot2, dot3, dot4, dot5, dot6, dot7, dot8, dot9, dot10, dot11, dot12);
            main.getChildren().add(atoms);


            Camera camera = new PerspectiveCamera();
            Scene scene = new Scene(main, WIDTH, HEIGHT, true);
            scene.setFill(Color.CORNFLOWERBLUE);
            scene.setCamera(camera);

            main.translateXProperty().set(WIDTH/2);
            main.translateYProperty().set(HEIGHT/2);
            main.translateZProperty().set(-100);

            initMouseControl(main, scene, primaryStage);

            primaryStage.setTitle("Structure Generation");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

    }


}
