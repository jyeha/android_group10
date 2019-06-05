package edu.skku.team10;

import java.util.List;

public class OpenWeatherMapJSON {
    public Coord coord;
    public Sys sys;
    public List<Weather> weather;
    public Main main;
    public Wind wind;
    public String name;


    public class Main {
        public double temp;
        public int humidity;
    }

    public class Coord {
        public double lon;
        public double lat;
    }

    public class Sys {
        public String country;
    }

    public class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    public class Wind {
        public double speed;
        public double deg;
    }
}

