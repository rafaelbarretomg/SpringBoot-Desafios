package challenges.challenge02_todolist.assemblers;

import challenges.challenge02_todolist.controllers.TodolistController;
import challenges.challenge02_todolist.models.Todolist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TodolistAssembler extends RepresentationModelAssemblerSupport<Todolist, Todolist> {

    public TodolistAssembler(){
        super(TodolistController.class, Todolist.class);
    }

    @Override
    public Todolist toModel(Todolist entity){
        entity.add(linkTo(methodOn(TodolistController.class).findById(entity.getId())).withSelfRel());
        return entity;
    }

    public PagedModel<Todolist> toPagedModel(Page<Todolist> page){
        //Converte o conteudo da pagina em uma lista de recursos com links
        return PagedModel.of(
                page.getContent().stream().map(this::toModel).toList(),
                new PagedModel.PageMetadata(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                ),
                linkTo(methodOn(TodolistController.class).findAll(PageRequest.of(0, page.getSize()))).withSelfRel()
        );
    }


}
