package flightlive.geometry;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import flightlive.model.Flight;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.net.URL;

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
    public Cylinder createLine(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(0.01f, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }

    // TODO: javadoc
    public void createPlane(Group planeGroup, String id, float latitude, float longitude, float angle, PhongMaterial material) {

        // Load geometry
        ObjModelImporter planeImporter = new ObjModelImporter();
        try {
            URL modelUrl = getClass().getResource("/flightlive/res/Plane/plane.obj");
            planeImporter.read(modelUrl);
        } catch(ImportException e) {
            System.err.println(e.getMessage());
        }
        MeshView[] planeMeshViews = planeImporter.getImport();

        for (MeshView mv : planeMeshViews) {
            mv.setMaterial(material);
        }

        Fx3DGroup planeScale = new Fx3DGroup(planeMeshViews);
        Fx3DGroup planeOffset = new Fx3DGroup(planeScale);
        Fx3DGroup plane = new Fx3DGroup(planeOffset);
        plane.setId(id);

        Point3D position = geoCoordTo3dCoord(latitude, longitude);

        planeScale.set3DScale(0.015);
        planeOffset.set3DTranslate(0, -0.01, 0);
        planeOffset.set3DRotate(0, 180 + angle, 0);
        plane.set3DTranslate(position.getX(),position.getY(),position.getZ());
        plane.set3DRotate(
                -java.lang.Math.toDegrees(java.lang.Math.asin(position.getY())) - 90,
                java.lang.Math.toDegrees(java.lang.Math.atan2(position.getX(), position.getZ())),
                0);

        planeGroup.getChildren().add(plane);
    }


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


    public void coord3dToGeoCoord(Point3D p) {
        double lat_cor = java.lang.Math.toDegrees(java.lang.Math.asin(-p.getY()));
        float lat = (float)lat_cor - TEXTURE_LAT_OFFSET;
        double lon_cor = java.lang.Math.toDegrees(java.lang.Math.acos(p.getZ() / java.lang.Math.cos(java.lang.Math.asin(-p.getY()))));
        float lon = (float)lon_cor - TEXTURE_LON_OFFSET;
        System.err.println("LATITUDE: " + lat);
        System.err.println("LONGITUDE: " + lon);

    }


    public void displayTown(Group parent, String name, float latitude, float longitude, PhongMaterial material) {
        Sphere sphere = new Sphere(0.002);
        sphere.setId(name);
        sphere.setMaterial(material);
        Point3D city = geoCoordTo3dCoord(latitude, longitude);
        sphere.setTranslateX(city.getX());
        sphere.setTranslateY(city.getY());
        sphere.setTranslateZ(city.getZ());
        parent.getChildren().add(sphere);
    }


    public void displayPath(Flight flight, Fx3DGroup plane, Group pathGroup, PhongMaterial materialL, PhongMaterial materialM, PhongMaterial materialH) {
        Sphere sphere;
        Point3D posSphere;
        double lat = 0, lon = 0;

        // Change size of the plane object
        plane.set3DScale(2.0);

        // Create the path
        pathGroup.getChildren().clear();
        double[] posHistory = flight.getCot();
        if(posHistory != null) {
            for (int i = 0; i < posHistory.length; i++) {
                // Create one point of the path with an old position
                if ((i + 1) % 4 == 0) {
                    sphere = new Sphere(0.003);
                    if (posHistory[i] < 400)
                        sphere.setMaterial(materialL);
                    else if (posHistory[i] < 600)
                        sphere.setMaterial(materialM);
                    else
                        sphere.setMaterial(materialH);
                    posSphere = geoCoordTo3dCoord((float) lat, (float) lon);
                    sphere.setTranslateX(posSphere.getX());
                    sphere.setTranslateY(posSphere.getY());
                    sphere.setTranslateZ(posSphere.getZ());
                    pathGroup.getChildren().add(sphere);
                } else {
                    if (i % 4 == 0)
                        lat = posHistory[i];
                    else if (i % 4 == 1)
                        lon = posHistory[i];
                }
            }
        }
    }

    // TODO: javadoc
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
