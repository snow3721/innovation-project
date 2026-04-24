package com.innovation.service;

import java.util.Map;

public interface StatisticsService {

    Map<String, Object> getOverview();

    Map<String, Object> getByYear(Integer year);

    Map<String, Object> getByCollege(Integer collegeId);

    Map<String, Object> getByCategory();
}
