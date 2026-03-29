package com.vnu.uet.service;

import com.vnu.uet.domain.Node;
import com.vnu.uet.repository.NodeRepository;
import com.vnu.uet.service.dto.NodeDTO;
import com.vnu.uet.service.mapper.NodeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.vnu.uet.domain.Node}.
 */
@Service
@Transactional
public class NodeService {

    private static final Logger LOG = LoggerFactory.getLogger(NodeService.class);

    private final NodeRepository nodeRepository;

    private final NodeMapper nodeMapper;

    public NodeService(NodeRepository nodeRepository, NodeMapper nodeMapper) {
        this.nodeRepository = nodeRepository;
        this.nodeMapper = nodeMapper;
    }

    /**
     * Save a node.
     *
     * @param nodeDTO the entity to save.
     * @return the persisted entity.
     */
    public NodeDTO save(NodeDTO nodeDTO) {
        LOG.debug("Request to save Node : {}", nodeDTO);
        Node node = nodeMapper.toEntity(nodeDTO);
        node = nodeRepository.save(node);
        return nodeMapper.toDto(node);
    }

    /**
     * Update a node.
     *
     * @param nodeDTO the entity to save.
     * @return the persisted entity.
     */
    public NodeDTO update(NodeDTO nodeDTO) {
        LOG.debug("Request to update Node : {}", nodeDTO);
        Node node = nodeMapper.toEntity(nodeDTO);
        node = nodeRepository.save(node);
        return nodeMapper.toDto(node);
    }

    /**
     * Partially update a node.
     *
     * @param nodeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NodeDTO> partialUpdate(NodeDTO nodeDTO) {
        LOG.debug("Request to partially update Node : {}", nodeDTO);

        return nodeRepository
            .findById(nodeDTO.getId())
            .map(existingNode -> {
                nodeMapper.partialUpdate(existingNode, nodeDTO);

                return existingNode;
            })
            .map(nodeRepository::save)
            .map(nodeMapper::toDto);
    }

    /**
     * Get all the nodes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NodeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Nodes");
        return nodeRepository.findAll(pageable).map(nodeMapper::toDto);
    }

    /**
     * Get one node by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NodeDTO> findOne(Long id) {
        LOG.debug("Request to get Node : {}", id);
        return nodeRepository.findById(id).map(nodeMapper::toDto);
    }

    /**
     * Delete the node by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Node : {}", id);
        nodeRepository.deleteById(id);
    }
}
