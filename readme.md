# sw360wsImport

This project is a subproject for sw360, https://github.com/eclipse/sw360. Project adds a feature to sw360 to import project data from WhiteSource using its REST API. This project contains only the backend service for the importing and fronend portlet is in the main project.

## Deploy
Portlet can be added from Liferay GUI and backend service must be imported manually (build and copy war file).

## How to use
In sw360wsImport portlet you must enter your Whitesource REST endpoint URL and you organization API key. Then you can select the product and after that you see all the projects under that product. You can select one or more projects which you can import by clicking the 'import' button.

## License
SPDX Short Identifier: http://spdx.org/licenses/EPL-1.0

SPDX-License-Identifier: EPL-1.0

All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
