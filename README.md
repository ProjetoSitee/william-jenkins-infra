# william-jenkins-infra

Configuração versionada do Jenkins para executar o pipeline do repositório `ProjetoSitee/william-java`.

## Conteúdo

- `Dockerfile`: imagem Jenkins LTS com Java 21, Docker e plugins.
- `plugins.txt`: plugins instalados automaticamente.
- `casc/jenkins.yaml`: segurança e ferramentas via Jenkins Configuration as Code.
- `compose.yaml`: definição reproduzível do serviço.
- `vars/standardPipeline.groovy`: Shared Library reutilizável para aplicações Java.

## Segurança

O arquivo `.env` e quaisquer chaves são ignorados pelo Git. Na AWS, os segredos deverão ficar no Jenkins Credentials e/ou AWS Secrets Manager, nunca no GitHub.

## Próxima etapa

Depois de escolher onde o Jenkins será hospedado, conecte um Pipeline Multibranch a:

`https://github.com/ProjetoSitee/william-java.git`

A infraestrutura AWS será adicionada separadamente para manter criação e custos explícitos.

## Uso na aplicação

O Jenkinsfile da aplicação importa `william-devops-pipelines` e chama `standardPipeline` com Java 21, Spring Boot e criação de imagem habilitados. A lógica comum permanece centralizada neste repositório.
