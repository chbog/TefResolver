package com.kpi.tefresolver.service;

import com.kpi.tefresolver.exception.ReportNotFoundException;
import com.kpi.tefresolver.model.ObservationData;
import com.kpi.tefresolver.model.Report;
import com.kpi.tefresolver.repository.ReportRepository;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariatePeriodicInterpolator;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@Service
public class InterpolationService {
    private ReportRepository reportRepository;
    private ApplicationContext applicationContext;


    public Report interpolateReport(Long reportId, String method) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException(reportId));
        if (report.getCorrect()) {
            return report;
        }
        restoreObservationTime(report);
        UnivariateInterpolator interpolator = (UnivariateInterpolator) applicationContext.getBean(method);
        restoreTemperature(report, interpolator);
        restoreWindSpeed(report, interpolator);

        report.setCorrect(true);
        return reportRepository.save(report);
    }

    protected void restoreObservationTime(Report report) {
        List<ObservationData> data = report.getRows();
        LocalTime currentTime = LocalTime.MIN;
        int currentDay = 0;
//        for (int i = 0; (i < data.size() || currentTime != LocalTime.MIN) && currentDay < DAYS_IN_CURR_MONTH; i++) {
        for (int i = 0; i < data.size(); i++) {
            if (currentTime == LocalTime.MIN) {
                ++currentDay;
            }
            if (!currentTime.equals(data.get(i).getObservationTime())) {
                ObservationData row = ObservationData.builder().dayNumber(currentDay).observationTime(currentTime).report(report).build();
                data.add(i, row);
            }
            currentTime = currentTime.plusMinutes(30);
        }
    }

    protected void restoreTemperature(Report report, UnivariateInterpolator interpolator) {
        List<Double> temperatures = report.getRows().stream()
                .map(i -> {
                    if(i.getTemperature() == null){
                        return null;
                    }
                    return Double.valueOf(i.getTemperature());
                })
                .collect(Collectors.toList());

        interpolator = setUpInterpolator(temperatures, interpolator);
        interpolateCells(temperatures, interpolator);
        List<Integer> roundedTemperatures = roundCellValues(temperatures);
        IntStream.range(0, roundedTemperatures.size())
                .filter(i -> report.getRows().get(i).getTemperature() == null)
                .forEach(i -> report.getRows().get(i).setTemperature(roundedTemperatures.get(i)));
    }

    protected <T> UnivariateInterpolator setUpInterpolator(List<T> cells, UnivariateInterpolator interpolator){
        int maxEmptyBorderPeriod = calculateMaxEmptyBorderPeriod(cells);
        if (maxEmptyBorderPeriod > 0) {
            interpolator = (UnivariateInterpolator) applicationContext.getBean("emptyBordersInterpolator", interpolator, maxEmptyBorderPeriod, 1);
        }
        return interpolator;
    }

//    protected void restoreTemperature(Report report, UnivariateInterpolator interpolator) {
//        List<Double> temperatures = report.getRows().stream()
//                .map(i -> {
//                    if(i.getTemperature() == null){
//                        return null;
//                    }
//                    return Double.valueOf(i.getTemperature());
//                })
//                .collect(Collectors.toList());
//
//        int maxEmptyBorderPeriod = calculateMaxEmptyBorderPeriod(temperatures);
//        if (maxEmptyBorderPeriod > 0) {
//            interpolator = (UnivariateInterpolator) applicationContext.getBean("emptyBordersInterpolator", interpolator, maxEmptyBorderPeriod, 1);
//        }
//
//        interpolateCells(temperatures, interpolator);
//        List<Integer> formattedInterpolatedTemperature = temperatures.stream()
//                .map(i -> (int) Math.round(i))
//                .collect(Collectors.toList());
//        IntStream.range(0, formattedInterpolatedTemperature.size())
//                .filter(i -> report.getRows().get(i).getTemperature() == null)
//                .forEach(i -> report.getRows().get(i).setTemperature(formattedInterpolatedTemperature.get(i)));
//    }

    protected void interpolateCells(List<Double> cells, UnivariateInterpolator interpolator){
        double[] x = IntStream.range(0, cells.size()).filter(i -> cells.get(i) != null).mapToDouble(i -> (double) i).toArray();
        double[] y = cells.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).toArray();
        UnivariateFunction interpolationFunction = interpolator.interpolate(x, y);
        IntStream.range(0, cells.size()).forEach(i -> {
            if(cells.get(i) == null){
                cells.set(i, interpolationFunction.value(i));
            }
        });
    }

    protected List<Integer> roundCellValues(List<Double> cells){
        return cells.stream()
                .map(i -> (int) Math.round(i))
                .collect(Collectors.toList());
    }

    protected <T> int calculateMaxEmptyBorderPeriod(List<T> elements) {
        int startPeriod = 0;
        for (T element : elements) {
            if (element != null) {
                break;
            }
            ++startPeriod;
        }

        if (startPeriod == elements.size()) {
            throw new IllegalArgumentException("There are completely empty column");
        }

        int endPeriod = 0;
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (elements.get(i) != null) {
                break;
            }
            ++endPeriod;
        }
        return Math.max(startPeriod, endPeriod);
    }

    protected void restoreWindSpeed(Report report, UnivariateInterpolator interpolator) {
        List<Double> speeds = report.getRows().stream()
                .map(i -> {
                    if(i.getWindSpeed() == null){
                        return null;
                    }
                    return Double.valueOf(i.getWindSpeed());
                })
                .collect(Collectors.toList());

        interpolator = setUpInterpolator(speeds, interpolator);
        interpolateCells(speeds, interpolator);
        List<Integer> roundedSpeeds = roundCellValues(speeds);
        IntStream.range(0, roundedSpeeds.size())
                .filter(i -> report.getRows().get(i).getWindSpeed() == null)
                .forEach(i -> report.getRows().get(i).setWindSpeed(roundedSpeeds.get(i)));
    }


    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    @Autowired
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
