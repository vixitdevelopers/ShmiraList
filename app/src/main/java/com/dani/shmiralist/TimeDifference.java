package com.dani.shmiralist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class TimeDifference {
    int days;
    int hours;
    int minutes;

    public TimeDifference(String start, String end) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date startDate = sdf.parse(start);
        Date endDate = sdf.parse(end);

        long difference = endDate.getTime() - startDate.getTime();
        if (difference < 0) {
            Date dateMax = sdf.parse("24:00");
            Date dateMin = sdf.parse("00:00");
            difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
        }
        days = (int) (difference / (1000 * 60 * 60 * 24));
        hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        minutes = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
    }

    public static String addTime(String start, int addedMin) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date startDate = sdf.parse(start);
        long millisToAdd = addedMin*60_000;
        startDate.setTime(startDate.getTime()+millisToAdd);
        return sdf.format(startDate);
    }
    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getTotalInMin(){
        int total = hours*60;
        total += minutes;
        return total;
    }
    public String getStringDifference(){
        String difference = "";
        if (hours>0&&minutes>0)
            difference = difference+ hours +" שעות ו-" +minutes +" דקות ";
        else if (hours>0)
            difference = hours +" שעות ";
        else if (minutes>0)
            difference = minutes + " דקות ";
        else
            difference = " 0 שעות";
        return difference;

    }

}