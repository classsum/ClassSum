import tempfile
import os
from lxml.etree import _Element
from lxml import etree

Element = _Element


def element_to_text(e: Element, encoding='utf-8'):
    if e is not None:
        return etree.tostring(e, encoding=encoding, method='text', pretty_print=True).decode(encoding).strip()
    else:
        return ""


def adb_shell(cmd):
    result = os.popen(cmd).read()
    return result


def get_clear_xmln(xml_text):
    s = xml_text.find("xmln")
    e = xml_text.find("revision")
    return xml_text[0:s] + xml_text[e:]


def src2xml(method_string: str, clear_xmln=True) -> str:
    tmp = tempfile.NamedTemporaryFile(mode='w+', suffix='.java', delete=False)
    tmp.write(method_string)
    tmp.close()
    cmd = "srcml {0}".format(tmp.name)
    ret = adb_shell(cmd)
    os.remove(tmp.name)
    if clear_xmln:
        return get_clear_xmln(ret)
    return ret


def reduce(root: Element):
    bodies = root.xpath("//block")
    for body in bodies:
        assert body.tag == "block"
        parent = body.getparent()
        if parent.getparent().tag == "unit" and parent.tag == "class":
            pass
        else:
            parent.remove(body)

    for init in root.xpath("//init"):
        init.getparent().remove(init)


if __name__ == '__main__':
    xml = src2xml(open('test.java').read())
    tree = etree.fromstring(xml.encode('utf-8'))
    reduce(tree)
    print(element_to_text(tree))
