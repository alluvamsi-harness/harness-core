package io.harness.ng.core.dto.filestore.mapper;

import io.harness.filter.dto.FilterPropertiesDTO;
import io.harness.filter.entity.FilterProperties;
import io.harness.filter.mapper.FilterPropertiesMapper;
import io.harness.ng.core.dto.filestore.filter.FilesFilterProperties;
import io.harness.ng.core.dto.filestore.filter.FilesFilterPropertiesDTO;
import io.harness.ng.core.mapper.TagMapper;

import com.google.inject.Singleton;
import org.modelmapper.ModelMapper;

@Singleton
public class FilesFilterPropertiesMapper
    implements FilterPropertiesMapper<FilesFilterPropertiesDTO, FilesFilterProperties> {
  @Override
  public FilterPropertiesDTO writeDTO(FilterProperties filterProperties) {
    ModelMapper modelMapper = new ModelMapper();
    FilterPropertiesDTO filterPropertiesDTO = modelMapper.map(filterProperties, FilesFilterPropertiesDTO.class);
    filterPropertiesDTO.setTags(TagMapper.convertToMap(filterProperties.getTags()));
    return filterPropertiesDTO;
  }

  @Override
  public FilterProperties toEntity(FilterPropertiesDTO filterPropertiesDTO) {
    ModelMapper modelMapper = new ModelMapper();
    FilesFilterProperties filterProperties = modelMapper.map(filterPropertiesDTO, FilesFilterProperties.class);
    filterProperties.setType(filterPropertiesDTO.getFilterType());
    filterProperties.setTags(TagMapper.convertToList(filterPropertiesDTO.getTags()));
    return filterProperties;
  }
}
