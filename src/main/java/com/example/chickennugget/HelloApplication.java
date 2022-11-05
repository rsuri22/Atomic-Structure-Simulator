package com.example.chickennugget;

import javafx.animation.FillTransition;
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
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.scene.control.Slider;
import javafx.animation.Timeline;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import java.io.IOException;
import java.io.SerializablePermission;

public class HelloApplication extends Application {

    public static final float WIDTH = 1400;
    public static final float HEIGHT = 800;
    public static final float RADIUS = 50;
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    @Override
    public void start(Stage primaryStage) throws Exception {

        Sphere sphere1 = new Sphere(RADIUS);
        sphere1.translateYProperty().set(RADIUS * 6);
        colorSphere(sphere1, Color.GREEN);

        Sphere sphere2 = new Sphere(RADIUS);
        colorSphere(sphere2, Color.RED);

        Sphere sphere3 = new Sphere(RADIUS);
        sphere3.translateXProperty().set(RADIUS * 6);
        colorSphere(sphere3, Color.MAGENTA);

        Cylinder cylinder1 = new Cylinder(RADIUS/5, RADIUS * 4);
        cylinder1.translateYProperty().set(RADIUS * 3);

        Cylinder cylinder2 = new Cylinder(RADIUS/5, RADIUS * 4);
        cylinder2.translateXProperty().set(RADIUS * 3);
        cylinder2.setRotationAxis(Rotate.Z_AXIS);
        cylinder2.rotateProperty().set(90);

        Cylinder cylinder3 = new Cylinder(RADIUS/5, RADIUS * 4);
        cylinder3.translateXProperty().set(RADIUS * -3);
        cylinder3.setRotationAxis(Rotate.Z_AXIS);
        cylinder3.rotateProperty().set(90);


        Group group = new Group();
        group.getChildren().add(sphere1);
        group.getChildren().add(cylinder1);
        group.getChildren().add(sphere2);
        group.getChildren().add(sphere3);
        group.getChildren().add(cylinder2);
        group.getChildren().add(cylinder3);


        Camera camera = new PerspectiveCamera();
        Scene scene = new Scene(group, WIDTH, HEIGHT);
        scene.setFill(Color.CORNFLOWERBLUE);
        scene.setCamera(camera);

        group.translateXProperty().set(WIDTH/2);
        group.translateYProperty().set(HEIGHT/2);
        group.translateZProperty().set(-100);

        initMouseControl(group, scene, primaryStage);

        primaryStage.setTitle("Structure Generation");
        primaryStage.setScene(scene);
        primaryStage.show();
        group.setRotationAxis(new Point3D(500, 350, 0));
    }

    private Sphere colorSphere(Sphere s, Color c){
        PhongMaterial color = new PhongMaterial(c);
        s.setMaterial(color);
        return s;
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