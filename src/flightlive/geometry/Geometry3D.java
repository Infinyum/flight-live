package flightlive.geometry;

import flightlive.controller.CameraManager;
import flightlive.model.Flight;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.net.URL;

import static javafx.scene.SceneAntialiasing.DISABLED;


public class Geometry3D {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- ATTRIBUTES ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */
    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */

    // From Rahel Lüthy : https://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
    public Cylinder createLine(Point3D origin, Point3D target, double scale) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(0.005f * scale, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }


    /**
     * Adds a plane to planeGroup based on the geometry plane.obj
     * @param planeGroup group in which to add the plane
     * @param id id of the plane which corresponds to the flight's id
     * @param latitude current latitude of the plane
     * @param longitude current longitude of the plane
     * @param angle current angle of the plane
     * @param material material used to color the plane
     * @param scale value to adjust the plane's size
     */
    public void createPlane(Group planeGroup, String id, float latitude, float longitude, float angle, PhongMaterial material, double scale) {
        // Load geometry
        ObjModelImporter planeImporter = new ObjModelImporter();
        try {
            URL modelUrl = getClass().getResource("/flightlive/res/Plane/plane.obj");
            planeImporter.read(modelUrl);
        } catch(ImportException e) {
            System.err.println(e.getMessage());
        }
        MeshView[] planeMeshViews = planeImporter.getImport();

        // Setting the material
        for (MeshView mv : planeMeshViews)
            mv.setMaterial(material);

        Fx3DGroup planeScale = new Fx3DGroup(planeMeshViews);
        Fx3DGroup planeOffset = new Fx3DGroup(planeScale);
        Fx3DGroup plane = new Fx3DGroup(planeOffset);

        Point3D position = geoCoordTo3dCoord(latitude, longitude);
        // Transformations on the object
        planeScale.set3DScale(0.015);
        planeOffset.set3DTranslate(0, -0.01, 0);
        planeOffset.set3DRotate(0, 180 + angle, 0);

        plane.set3DTranslate(position.getX(),position.getY(),position.getZ());
        plane.set3DRotate(
                -java.lang.Math.toDegrees(java.lang.Math.asin(position.getY())) - 90,
                java.lang.Math.toDegrees(java.lang.Math.atan2(position.getX(), position.getZ())),
                0);
        plane.set3DScale(scale);
        plane.setId(id);

        planeGroup.getChildren().add(plane);
    }


    /**
     * Adds the earth 3D object on the pane
     * @param pane the pane in which the earth is placed
     * @param root3D the group containing all the 3D
     * @return the group containing the earth
     */
    public Group setEarth(Pane pane, Group root3D) {
        // Load geometry
        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            URL modelUrl = this.getClass().getResource("/flightlive/res/Earth/earth.obj");
            objImporter.read(modelUrl);
        } catch(ImportException e) {
            System.out.println(e.getMessage());
        }
        MeshView[] meshViews = objImporter.getImport();
        Group earth = new Group(meshViews);
        root3D.getChildren().add(earth);

        // Add a camera group
        PerspectiveCamera camera = new PerspectiveCamera(true);
        new CameraManager(camera, pane, root3D);

        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(root3D);
        root3D.getChildren().add(ambientLight);

        // Creating a subscene
        SubScene subScene = new SubScene(root3D, 500, 500, true, DISABLED);
        subScene.setCamera(camera);
        subScene.setFill(Color.rgb(248, 249, 250));
        pane.getChildren().add(subScene);
        return earth;
    }


    /**
     * Conversion from latitude and longitude to 3D xyz coordinates
     * @param lat a given latitude
     * @param lon a given longitude
     * @return a matching point in the 3D space
     */
    public Point3D geoCoordTo3dCoord(float lat, float lon) {
        float lat_cor = lat + TEXTURE_LAT_OFFSET;
        float lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)),
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor)),
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)));
    }


    /**
     * Conversion from 3D xyz coordinates to latitude and longitude on the globe
     * @param p a given point in 3D space
     * @return the corresponding latitude and longitude using the Position class
     */
    public Position coord3dToGeoCoord(Point3D p) {
        double lat_cor = java.lang.Math.toDegrees(java.lang.Math.asin(-p.getY()));
        float lat = (float)lat_cor - TEXTURE_LAT_OFFSET;

        // First case issue: longitude between 0° and 180° => half the globe
        double lon_cor = java.lang.Math.toDegrees(java.lang.Math.acos(p.getZ()
                / java.lang.Math.cos(java.lang.Math.asin(-p.getY()))));

        // Second case issue: longitude between -90° and 90° => half the globe too
        //double lon_cor = java.lang.Math.toDegrees(java.lang.Math.asin(-p.getX()
        //        / java.lang.Math.cos(java.lang.Math.asin(-p.getY()))));

        float lon = (float)lon_cor - TEXTURE_LON_OFFSET;

        return new Position(lat, lon);
    }


    /**
     * Adds a city (understand sphere) in the given parent group
     * @param parent a group in which to add the sphere
     * @param name name of the city
     * @param latitude latitude of the city
     * @param longitude longitude of the city
     * @param material material to customize the sphere
     * @param scale size of the sphere
     */
    public void displayTown(Group parent, String name, float latitude, float longitude, PhongMaterial material, double scale) {
        Sphere sphere = new Sphere(0.002);
        Point3D city = geoCoordTo3dCoord(latitude, longitude);

        // Transformations on the sphere
        sphere.setTranslateX(city.getX());
        sphere.setTranslateY(city.getY());
        sphere.setTranslateZ(city.getZ());
        sphere.setScaleX(scale);
        sphere.setScaleY(scale);
        sphere.setScaleZ(scale);
        sphere.setMaterial(material);

        sphere.setId(name);

        parent.getChildren().add(sphere);
    }


    /**
     * Displays the trajectory of the given flight
     * @param flight a given flight
     * @param plane the corresponding plane object
     * @param pathGroup group in which to add the trajectory shapes
     * @param materialL material for low speed
     * @param materialH material for higher speed
     * @param scale size of the path
     */
    public void displayPath(Flight flight, Fx3DGroup plane, Group pathGroup, PhongMaterial materialL, PhongMaterial materialH, double scale) {
        Cylinder tmp;
        Point3D posOrigin = null, posTarget = null;
        double lat = 0, lon = 0;

        // Change size of the plane object
        plane.set3DScale(2.0);

        // Create the path
        pathGroup.getChildren().clear();
        double[] posHistory = flight.getCot();

        if (posHistory != null) {
            for (int i = 0; i < posHistory.length; i++) {
                // We collected all information about one position of the history
                if ((i + 1) % 4 == 0) {
                    posTarget = geoCoordTo3dCoord((float) lat, (float) lon);
                    // Create cylinder
                    if (posOrigin != null) {
                        tmp = createLine(posOrigin, posTarget, scale);
                        if (posHistory[i] < 400)
                            tmp.setMaterial(materialL);
                        else
                            tmp.setMaterial(materialH);
                        pathGroup.getChildren().add(tmp);
                    }
                    posOrigin = posTarget;
                } else {
                    if (i % 4 == 0)
                        lat = posHistory[i];
                    else if (i % 4 == 1)
                        lon = posHistory[i];
                }
            }
        }
    }


    /**
     * Draws a sphere with a given radius located at a given point
     * @param radiusGroup group in which to add the sphere
     * @param radius radius of the sphere
     * @param p a given point to set to center coordinates of the sphere
     */
    public void displayRadius(Group radiusGroup, int radius, Point3D p) {
        Sphere radiusIndicator = new Sphere();
        radiusIndicator.setTranslateX(p.getX());
        radiusIndicator.setTranslateY(p.getY());
        radiusIndicator.setTranslateZ(p.getZ());
        radiusIndicator.setRadius(0.02);
        radiusIndicator.setOpacity(0.2);
        //radiusIndicator.setMaterial(new PhongMaterial(new Color(1.0f, 0.f, 0.f, 0.1f)));
        //PhongMaterial material = new PhongMaterial();
        //material.setDiffuseColor(new Color(1.0f, 1.f, 1.f, 0.2f));
        //material.setSpecularColor(Color.INDIANRED);
        //radiusIndicator.setMaterial(material);
        radiusGroup.getChildren().add(radiusIndicator);
    }
}