package flightlive.geometry;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
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
    public void createPlane(Group planeGroup, float latitude, float longitude, float angle, PhongMaterial material) {

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

        //...

        Fx3DGroup planeScale = new Fx3DGroup(planeMeshViews);
        Fx3DGroup planeOffset = new Fx3DGroup(planeScale);
        Fx3DGroup plane = new Fx3DGroup(planeOffset);

        Point3D position = geoCoordTo3dCoord(latitude, longitude);

        planeScale.set3DScale(0.05);
        planeOffset.set3DTranslate(0, -0.01, 0);
        planeOffset.set3DRotate(0, 180 + angle, 0);
        plane.set3DTranslate(position.getX(),position.getY(),position.getZ());
        plane.set3DRotate(
                -java.lang.Math.toDegrees(java.lang.Math.asin(position.getY())) - 90,
                java.lang.Math.toDegrees(java.lang.Math.atan2(position.getX(), position.getZ())),
                0);

        planeGroup.getChildren().addAll(plane);
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


    public void displayTown(Group parent, String name, float latitude, float longitude, PhongMaterial material) {
        Sphere sphere = new Sphere(0.02);
        sphere.setMaterial(material);
        Point3D city = geoCoordTo3dCoord(latitude, longitude);
        sphere.setTranslateX(city.getX());
        sphere.setTranslateY(city.getY());
        sphere.setTranslateZ(city.getZ());
        parent.getChildren().add(sphere);
    }
}
