[![KubeRig Logo](https://github.com/teyckmans/kuberig/blob/master/docs/images/website_logo_transparent_background.png)](https://github.com/teyckmans/kuberig)

# kuberig-dsl-kubernetes

This project contains the vanilla kubernetes DSL generator projects.

## Details

The modules in this project are auto generated based on the kubernetes/kubernetes repository.

It scans all tags starting from v1.10.7. Which is the first version that contains the /api/openapi-spec/swagger.json file that we depend on for the DSL generation.

You can check the DslProjectsGeneratorTask in the buildSrc for more details.

## Versioning

This project follows the version of the https://github.com/teyckmans/kuberig-dsl project.