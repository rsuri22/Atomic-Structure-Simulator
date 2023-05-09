package com.example.chickennugget;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.transform.Affine;

public class Test extends Application {
    private static double mouseStartPosX, mouseStartPosY;
    private static double mousePosX, mousePosY;
    private static double mouseOldX, mouseOldY;
    private static double mouseDeltaX, mouseDeltaY;
    private static double MOUSE_SPEED = 0.1;
    private static double ROTATION_SPEED = 4.0;
    private static double anchorX, anchorY;
    private static double anchorAngleX = 0;
    private static double anchorAngleY = 0;
    private static final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private static final DoubleProperty angleY = new SimpleDoubleProperty(0);
    public static MeshView CreateSphere(double radius, int triangles) {

        TriangleMesh mesh = new TriangleMesh();
        double phimin = 0;
        double phimax = 6.28;
        double phi = phimin;
        double theta;
        double thetamin = -3.14;
        double thetamax = 2;

        for (int i = 0; i < triangles + 1; i++) {
            theta = thetamin;
            for (int j = 0; j < triangles + 1; j++) {
                Point3D p3D = new Point3D( (radius * Math.cos(theta) * Math.sin(phi)),
                        (radius * Math.cos(theta) * Math.cos(phi)),  (radius * Math.sin(theta)));
                mesh.getPoints().addAll((float) p3D.getX(), (float) p3D.getY(), (float) p3D.getZ());
                theta += (thetamax - thetamin) / triangles;
            }
            phi += (phimax - phimin) / triangles;
        }
        mesh.getTexCoords().addAll(0, 0);
        for (int i = 0; i < triangles; i++) {
            int multiplier = (i * triangles) + i;
            for (int j = multiplier; j < triangles + multiplier; j++) {
                mesh.getFaces().addAll(j, 0, j + 1, 0, j + triangles + 1, 0);
                mesh.getFaces().addAll(j + triangles + 1, 0, j + 1, 0, j + triangles + 2, 0);
            }
            for (int j = triangles + multiplier; j > multiplier; j--) {
                mesh.getFaces().addAll(j, 0, j - 1, 0, j + triangles + 1, 0);
                mesh.getFaces().addAll(j - 1, 0, j + triangles, 0, j + triangles + 1, 0);
            }
        }
        MeshView meshView = new MeshView(mesh);

        return meshView;
    }

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

    private static void initMouseControl2(Group group, Scene scene, Stage stage) {
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

    public void start(Stage primaryStage) throws Exception {

        MeshView m = CreateSphere(250, 30);

        Group main = new Group();
        main.getChildren().add(m);

        Camera camera = new PerspectiveCamera();
        Scene scene = new Scene(main, 1400, 800, true);
        scene.setFill(Color.CORNFLOWERBLUE);
        scene.setCamera(camera);

        main.translateXProperty().set(1400/2);
        main.translateYProperty().set(800/2);
        main.translateZProperty().set(-100);

        initMouseControl2(main, scene, primaryStage);

        primaryStage.setTitle("Structure Generation");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
