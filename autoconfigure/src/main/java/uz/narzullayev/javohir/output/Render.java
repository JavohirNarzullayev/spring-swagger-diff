package uz.narzullayev.javohir.output;


import uz.narzullayev.javohir.model.ChangedOpenApi;

public interface Render {

  String render(ChangedOpenApi diff);
}
