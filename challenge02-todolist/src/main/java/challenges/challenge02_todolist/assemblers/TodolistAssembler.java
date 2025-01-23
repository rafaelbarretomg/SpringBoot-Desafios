package challenges.challenge02_todolist.assemblers;

import challenges.challenge02_todolist.controllers.TodolistController;
import challenges.challenge02_todolist.models.Todolist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TodolistAssembler extends RepresentationModelAssemblerSupport<Todolist, Todolist> {

    public TodolistAssembler() {
        super(TodolistController.class, Todolist.class);
    }

    @Override
    public Todolist toModel(Todolist entity) {
        entity.add(linkTo(methodOn(TodolistController.class).findById(entity.getId())).withSelfRel());
        return entity;
    }

    public String getLink(Page<Todolist> page, Pageable pageable){
        StringBuilder sortParams = new StringBuilder();
        page.getSort().forEach(order -> {
            if (sortParams.length() > 0) sortParams.append("&");
            sortParams.append("sort=").append(order.getProperty()).append(",").append(order.getDirection());
        });

        String link = linkTo(methodOn(TodolistController.class).findAll(null)).toUri().toString()
                + "?page=" + pageable.getPageNumber()
                + "&size=" + pageable.getPageSize()
                + "&" + sortParams;

        return link;
    }

    public PagedModel<Todolist> toPagedModel(Page<Todolist> page) {
        //Converte o conteudo da pagina em uma lista de recursos com links
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                page.getSize(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );


        PagedModel<Todolist> pagedModel = PagedModel.of(
                page.getContent().stream().map(this::toModel).toList(),
                metadata
        );
        // Link para a página atual
        pagedModel.add(Link.of(getLink(page,page.getPageable())).withSelfRel());

        // Link para a primeira página
        pagedModel.add(Link.of(getLink(page,page.getPageable().first())).withRel("first"));

        // Link para a próxima página (se existir)
        if (page.hasNext()) {
            pagedModel.add(Link.of(getLink(page,page.nextPageable())).withRel("next"));
        }

        // Link para a página anterior (se existir)
        if (page.hasPrevious()) {
            pagedModel.add(Link.of(getLink(page,page.previousPageable())).withRel("previous"));
        }

        // Link para a última página
        pagedModel.add(Link.of(getLink(page,PageRequest.of(page.getTotalPages() -1, page.getPageable().getPageSize()))).withRel("last"));

        return pagedModel;

    }


}
