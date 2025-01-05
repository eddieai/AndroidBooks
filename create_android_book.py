from pathlib import Path
import shutil
from PIL import Image
import argparse


def create_new_android_book(
    book_name: str, url: str, package_name: str, icon_path: str
) -> None:
    """Create a new Android book project by copying and modifying a template.

    Args:
        book_name (str): Name of the new book project.
        url (str): URL to be used in the MainActivity.
        package_name (str): Package name for the Android project.
        icon_path (str): Path to the icon image file.
    """
    shutil.copytree("_template_book", book_name)

    strings_xml_path = Path(f"{book_name}/app/src/main/res/values/strings.xml")
    strings_xml_path.write_text(
        strings_xml_path.read_text(encoding="utf-8").replace(
            "_template_book", book_name
        ),
        encoding="utf-8",
    )

    MainActivity_java_path = Path(
        f"{book_name}/app/src/main/java/com/eddie/_template_book/MainActivity.java"
    )
    MainActivity_java_path.write_text(
        MainActivity_java_path.read_text(encoding="utf-8").replace(
            "https://www._template_book.com", url
        ),
        encoding="utf-8",
    )
    MainActivity_java_path.parent.rename(
        MainActivity_java_path.parent.parent / package_name
    )

    other_files_to_modify = [
        Path(f"{book_name}/app/build.gradle"),
        Path(f"{book_name}/app/src/main/AndroidManifest.xml"),
        Path(
            f"{book_name}/app/src/main/java/com/eddie/{package_name}/MainActivity.java"
        ),
    ]
    for file_path_to_modify in other_files_to_modify:
        file_path_to_modify.write_text(
            file_path_to_modify.read_text(encoding="utf-8").replace(
                "_template_book", package_name
            ),
            encoding="utf-8",
        )

    icon = Image.open(icon_path)
    icon_paths_to_replace = [
        Path(f"{book_name}/app/src/main/res/drawable-hdpi"),
        Path(f"{book_name}/app/src/main/res/drawable-mdpi"),
        Path(f"{book_name}/app/src/main/res/drawable-xhdpi"),
        Path(f"{book_name}/app/src/main/res/drawable-xxhdpi"),
    ]
    for icon_path_to_replace in icon_paths_to_replace:
        (icon_path_to_replace / "_template_book.png").unlink()
        icon.save(icon_path_to_replace / f"{package_name}.png", "PNG")

    Path(icon_path).unlink()


def main() -> None:
    """Main function to handle command-line arguments and create a new Android book."""
    parser = argparse.ArgumentParser(description="Create a new Android book project.")
    parser.add_argument("book_name", type=str, help="Name of the new book project.")
    parser.add_argument("url", type=str, help="URL to be used in the MainActivity.")
    parser.add_argument(
        "package_name", type=str, help="Package name for the Android project."
    )
    parser.add_argument("icon_path", type=str, help="Path to the icon image file.")

    args = parser.parse_args()

    create_new_android_book(args.book_name, args.url, args.package_name, args.icon_path)


if __name__ == "__main__":
    main()
