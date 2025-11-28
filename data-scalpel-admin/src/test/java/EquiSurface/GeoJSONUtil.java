package EquiSurface;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GeoJSONUtil {
    public static SimpleFeatureCollection readGeoJsonByString(String geojsonpogylon) throws IOException {
        FeatureJSON fjson = new FeatureJSON();
        SimpleFeatureCollection featureCollection = (SimpleFeatureCollection) fjson.readFeatureCollection(geojsonpogylon);
        return featureCollection;
    }

}
