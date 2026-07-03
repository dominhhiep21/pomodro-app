#!/bin/bash

# Axelor Module Generator Script
# Usage: ./.kiro/scripts/create-axelor-module.sh <module-name> [module-title] [package-name]

if [ $# -lt 1 ]; then
    echo "Usage: $0 <module-name> [module-title] [package-name]"
    echo "Example: $0 pvb-contact 'Contact Management' 'vn.com.pvcombank.contact'"
    exit 1
fi

# Get the project root directory (where the script is called from)
PROJECT_ROOT=$(pwd)

MODULE_NAME=$1
MODULE_TITLE=${2:-"Axelor :: ${MODULE_NAME}"}
PACKAGE_NAME=${3:-"vn.com.pvcombank"}
MODULE_DIR="${PROJECT_ROOT}/modules/${MODULE_NAME}"

echo "🚀 Creating Axelor module: ${MODULE_NAME}"
echo "📝 Module title: ${MODULE_TITLE}"
echo "📦 Package name: ${PACKAGE_NAME}"
echo "📁 Module directory: ${MODULE_DIR}"
echo ""

# Create directory structure with .gitkeep files
echo "📂 Creating directory structure..."
mkdir -p "${MODULE_DIR}/src/main/java/${PACKAGE_NAME//./\/}/web"
mkdir -p "${MODULE_DIR}/src/main/java/${PACKAGE_NAME//./\/}/service"
mkdir -p "${MODULE_DIR}/src/main/resources/domains"
mkdir -p "${MODULE_DIR}/src/main/resources/views"
mkdir -p "${MODULE_DIR}/src/main/resources/i18n"

# Add .gitkeep files to preserve empty directories
echo "📌 Adding .gitkeep files..."
touch "${MODULE_DIR}/src/main/java/${PACKAGE_NAME//./\/}/web/.gitkeep"
touch "${MODULE_DIR}/src/main/java/${PACKAGE_NAME//./\/}/service/.gitkeep"
touch "${MODULE_DIR}/src/main/resources/domains/.gitkeep"
touch "${MODULE_DIR}/src/main/resources/views/.gitkeep"
touch "${MODULE_DIR}/src/main/resources/i18n/.gitkeep"

# Create build.gradle
echo "⚙️ Creating build.gradle..."
cat > "${MODULE_DIR}/build.gradle" << EOF
plugins {
  id 'com.axelor.app'
}

axelor {
  title = "${MODULE_TITLE}"
}

dependencies {
  // Add module dependencies here
  // implementation project(':modules:other-module')
}
EOF

# Create Module class
echo "☕ Creating Module class..."
MODULE_CLASS_NAME=$(echo "${MODULE_NAME}" | sed 's/-//g' | sed 's/\b\w/\U&/g')Module
cat > "${MODULE_DIR}/src/main/java/${PACKAGE_NAME//./\/}/${MODULE_CLASS_NAME}.java" << EOF
package ${PACKAGE_NAME};

import com.axelor.app.AxelorModule;

public class ${MODULE_CLASS_NAME} extends AxelorModule {

  @Override
  protected void configure() {
    // Add service bindings here
    // bind(YourService.class).to(YourServiceImpl.class);
    
    // Add event listeners here
    // addHibernateListenerConfigurator(YourEventListenerConfigurator.class);
    
    // Add quick menus here
    // addQuickMenu(YourQuickMenu.class);
  }
}
EOF



# Create README for the module
echo "📖 Creating module README..."
cat > "${MODULE_DIR}/README.md" << EOF
# ${MODULE_TITLE}

## Overview
This module provides ${MODULE_NAME} functionality for Axelor Open Platform.

## Package Structure
- **Package:** \`${PACKAGE_NAME}\`
- **Domain Models:** \`src/main/resources/domains/\`
- **Views:** \`src/main/resources/views/\`
- **Controllers:** \`src/main/java/${PACKAGE_NAME//./\/}/web/\`
- **Services:** \`src/main/java/${PACKAGE_NAME//./\/}/service/\`

## Setup Instructions

1. Add to \`settings.gradle\`:
   \`\`\`gradle
   include 'modules:${MODULE_NAME}'
   \`\`\`

2. Add dependency to main \`build.gradle\`:
   \`\`\`gradle
   dependencies {
     implementation project(':modules:${MODULE_NAME}')
   }
   \`\`\`

3. Create your domain models in \`domains/\` directory
4. Create your views in \`views/\` directory

## Development

- Domain models: Define in \`domains/\` directory
- Views: Define in \`views/\` directory  
- Business logic: Implement in \`web/\` controllers
- Services: Implement in \`service/\` package

## Testing

Run tests with:
\`\`\`bash
./gradlew test
\`\`\`

Note: Test configuration should be at application level, not module level.
EOF

echo ""
echo "✅ Module structure created successfully!"
echo ""
echo "📋 Next steps:"
echo "1. Add to settings.gradle: include 'modules:${MODULE_NAME}'"
echo "2. Add to main build.gradle: implementation project(':modules:${MODULE_NAME}')"
echo "3. Create domain models in domains/ directory"
echo "4. Create views in views/ directory"
echo "5. Run: ./gradlew generateCode"
echo ""
echo "📁 Module structure:"
tree "${MODULE_DIR}" 2>/dev/null || find "${MODULE_DIR}" -type d | sed 's|[^/]*/|  |g'
echo ""
echo "📌 .gitkeep files added to preserve empty directories in Git"
echo ""
echo "ℹ️  Note: Test configuration should be at application level, not module level"
