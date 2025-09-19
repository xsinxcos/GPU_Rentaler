package com.gpu.rentaler.sys.service;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gpu.rentaler.common.BusinessException;
import com.gpu.rentaler.common.CommonResultStatus;
import com.gpu.rentaler.common.DomainEventPublisher;
import com.gpu.rentaler.common.authz.PermissionHelper;
import com.gpu.rentaler.sys.event.ResourceCreated;
import com.gpu.rentaler.sys.event.ResourceDeleted;
import com.gpu.rentaler.sys.event.ResourceUpdated;
import com.gpu.rentaler.sys.model.Resource;
import com.gpu.rentaler.sys.model.Resource.Type;
import com.gpu.rentaler.sys.repository.ResourceRepository;
import com.gpu.rentaler.sys.service.dto.MenuResourceDTO;
import com.gpu.rentaler.sys.service.dto.ResourceTreeDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gpu.rentaler.common.Constants.RESOURCE_ROOT_ID;

/**
 * @author cjbi
 */
@Service
public class ResourceService {

  private final ResourceRepository resourceRepository;

  public ResourceService(ResourceRepository resourceRepository) {
    this.resourceRepository = resourceRepository;
  }

  public Set<Resource> findResourceByIds(Set<Long> resourceIds) {
    return resourceRepository.findByIds(resourceIds);
  }

  public Resource findResourceById(Long resourceId) {
    return resourceRepository.findById(resourceId)
      .orElseThrow(() -> new BusinessException(CommonResultStatus.RECORD_NOT_EXIST));
  }

  public List<MenuResourceDTO> findMenus(Set<String> permissions) {
    Resource probe = new Resource();
    probe.setType(Type.MENU);
    List<Resource> allMenus = resourceRepository.findAll(Example.of(probe));
    List<MenuResourceDTO> list = new ArrayList<>();
    for (Resource menu : allMenus) {
      if (!PermissionHelper.hasPermission(permissions, menu.getPermission())) {
        continue;
      }
      list.add(new MenuResourceDTO(menu.getId(), menu.getName(), menu.getUrl(), menu.getIcon(), menu.getParent().getId()));
    }
    return list;
  }


  public List<ResourceTreeDTO> findResourceTree() {
    List<Resource> allResources = resourceRepository.findAll();
    return getResourceTree(allResources, RESOURCE_ROOT_ID);
  }

  private List<ResourceTreeDTO> getResourceTree(List<Resource> resources, Long parentId) {
    return resources.stream()
      .filter(r -> r.getParent() != null && r.getParent().getId().equals(parentId))
      .map(r -> new ResourceTreeDTO(r.getId(), r.getName(), r.getType(), r.getPermission(), r.getUrl(), r.getIcon(), getResourceTree(resources, r.getId()), r.getParent().getId(), r.getParent().getName()))
      .collect(Collectors.toList());
  }

  @Transactional
  public Resource createResource(String name, Type type, String url, String icon, String permission, Long parentId) {
    Resource resource = new Resource();
    resource.setName(name);
    resource.setType(type);
    resource.setUrl(url);
    resource.setIcon(icon);
    resource.setPermission(permission);
    resource.setParent(findResourceById(parentId));
    resource = resourceRepository.save(resource);
    DomainEventPublisher.instance().publish(new ResourceCreated(resource));
    return resource;
  }

  @Transactional
  public Resource updateResource(Long resourceId, String name, Type type, String url, String icon, String permission, Long parentId) {
    Resource resource = findResourceById(resourceId);
    resource.setName(name);
    resource.setType(type);
    resource.setUrl(url);
    resource.setIcon(icon);
    resource.setPermission(permission);
    resource.setParent(findResourceById(parentId));
    resource = resourceRepository.save(resource);
    DomainEventPublisher.instance().publish(new ResourceUpdated(resource));
    return resource;
  }

  @Transactional
  public void deleteResourceById(Long resourceId) {
    Resource resource = findResourceById(resourceId);
    resourceRepository.delete(resource);
    DomainEventPublisher.instance().publish(new ResourceDeleted(resource));
  }


}
