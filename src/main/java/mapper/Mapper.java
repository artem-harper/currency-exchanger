package mapper;

public interface Mapper<F, T>{

    T mapToDto(F object);
    F mapToEntity(T object);

}
