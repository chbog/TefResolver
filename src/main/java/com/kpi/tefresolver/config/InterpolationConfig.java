package com.kpi.tefresolver.config;

import org.apache.commons.math3.analysis.interpolation.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class InterpolationConfig {
    @Bean(name = "linear")
    LinearInterpolator getLinearInterpolator(){
        return new LinearInterpolator();
    }

    @Bean(name = "spline")
    SplineInterpolator getSplineInterpolator(){
        return new SplineInterpolator();
    }

    @Bean(name = "lagrange")
    DividedDifferenceInterpolator getLagrangeInterpolator(){
        return new DividedDifferenceInterpolator();
    }

    @Bean(name = "emptyBordersInterpolator")
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    UnivariatePeriodicInterpolator getEmptyBordersInterpolator(UnivariateInterpolator interpolator, double period, int extend){
        return new UnivariatePeriodicInterpolator(interpolator, period, extend);
    }
}
