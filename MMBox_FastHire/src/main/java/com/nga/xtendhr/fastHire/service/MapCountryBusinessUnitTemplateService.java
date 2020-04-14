package com.nga.xtendhr.fastHire.service;

import java.util.List;

import com.nga.xtendhr.fastHire.model.MapCountryBusinessUnitTemplate;

public interface MapCountryBusinessUnitTemplateService {
	
	public List<MapCountryBusinessUnitTemplate> findAll();
	public MapCountryBusinessUnitTemplate create(MapCountryBusinessUnitTemplate item);
	public MapCountryBusinessUnitTemplate findById(String id);
	public List<MapCountryBusinessUnitTemplate> findByCountryBusinessUnitId(String countryBusinessUnitId);
	public List<MapCountryBusinessUnitTemplate> findByCountryBusinessUnitTemplate(String countryBusinessUnitId, String templateId);
	public void deleteByObject(MapCountryBusinessUnitTemplate item);

}
