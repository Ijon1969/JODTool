package jp.ac.kyushu_u.csce.modeltool.base.utility;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

/**
 * XMLモデルのノードを表すクラス<br>
 * {@link org.w3c.dom.Node}のラッパークラス
 *
 * @see org.w3c.dom.Node
 *
 * @author KBK yoshimura
 */
public class XMLNode implements Node {

	/** ノード */
	private Node node;

	/**
	 * コンストラクタ
	 * @param node ノード
	 */
	public XMLNode(Node node) {
		this.node = node;
	}

	/**
	 * ノードの子要素の中で、指定されたタグ名を持つ要素の個数を取得する。
	 * @param tag タグ名
	 * @return 指定タグの要素数
	 */
	public int count(String tag) {
		int counter = 0;
		NodeList nodeList = node.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeName().equals(tag)) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * ノードの取得<br>
	 * 子要素の中で、指定されたタグ名に一致する最初のノードを返す。
	 * @param tag タグ名
	 * @return ノード
	 */
	public XMLNode n(String tag) {
		return n(tag, 0);
	}

	/**
	 * ノードの取得<br>
	 * 子要素の中で、指定されたタグ名に一致する <code>index</code>番目のノードを返す。
	 * @param tag タグ名
	 * @param index インデックス
	 * @return ノード
	 */
	public XMLNode n(String tag, int index) {
		int counter = 0;
		NodeList nodeList = node.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeName().equals(tag)) {
				if (counter == index) {
					return new XMLNode(nodeList.item(i));
				}
				counter++;
			}
		}
		return null;
	}

	////// Nodeのラップ //////
	/**
	 * @see org.w3c.dom.Node#appendChild(Node)
	 */
	public Node appendChild(Node newChild) throws DOMException {
		return node.appendChild(newChild);
	}

	/**
	 * @see org.w3c.dom.Node#cloneNode(boolean)
	 */
	public Node cloneNode(boolean deep) {
		return node.cloneNode(deep);
	}

	/**
	 * @see org.w3c.dom.Node#compareDocumentPosition(Node)
	 */
	public short compareDocumentPosition(Node other) throws DOMException {
		return node.compareDocumentPosition(other);
	}

	/**
	 * @see org.w3c.dom.Node#getAttributes()
	 */
	public NamedNodeMap getAttributes() {
		return node.getAttributes();
	}

	/**
	 * @see org.w3c.dom.Node#getBaseURI()
	 */
	public String getBaseURI() {
		return node.getBaseURI();
	}

	/**
	 * @see org.w3c.dom.Node#getChildNodes()
	 */
	public NodeList getChildNodes() {
		return node.getChildNodes();
	}

	/**
	 * @see org.w3c.dom.Node#getFeature(String, String)
	 */
	public Object getFeature(String feature, String version) {
		return node.getFeature(feature, version);
	}

	/**
	 * @see org.w3c.dom.Node#getFirstChild()
	 */
	public Node getFirstChild() {
		return node.getFirstChild();
	}

	/**
	 * @see org.w3c.dom.Node#getLastChild()
	 */
	public Node getLastChild() {
		return node.getLastChild();
	}

	/**
	 * @see org.w3c.dom.Node#getLocalName()
	 */
	public String getLocalName() {
		return node.getLocalName();
	}

	/**
	 * @see org.w3c.dom.Node#getNamespaceURI()
	 */
	public String getNamespaceURI() {
		return node.getNamespaceURI();
	}

	/**
	 * @see org.w3c.dom.Node#getNextSibling()
	 */
	public Node getNextSibling() {
		return node.getNextSibling();
	}

	/**
	 * @see org.w3c.dom.Node#getNodeName()
	 */
	public String getNodeName() {
		return node.getNodeName();
	}

	/**
	 * @see org.w3c.dom.Node#getNodeType()
	 */
	public short getNodeType() {
		return node.getNodeType();
	}

	/**
	 * @see org.w3c.dom.Node#getNodeValue()
	 */
	public String getNodeValue() throws DOMException {
		return node.getNodeValue();
	}

	/**
	 * @see org.w3c.dom.Node#getOwnerDocument()
	 */
	public Document getOwnerDocument() {
		return node.getOwnerDocument();
	}

	/**
	 * @see org.w3c.dom.Node#getParentNode()
	 */
	public Node getParentNode() {
		return node.getParentNode();
	}

	/**
	 * @see org.w3c.dom.Node#getPrefix()
	 */
	public String getPrefix() {
		return node.getPrefix();
	}

	/**
	 * @see org.w3c.dom.Node#getPreviousSibling()
	 */
	public Node getPreviousSibling() {
		return node.getPreviousSibling();
	}

	/**
	 * @see org.w3c.dom.Node#getTextContent()
	 */
	public String getTextContent() throws DOMException {
		return node.getTextContent();
	}

	/**
	 * @see org.w3c.dom.Node#getUserData(String)
	 */
	public Object getUserData(String key) {
		return node.getUserData(key);
	}

	/**
	 * @see org.w3c.dom.Node#hasAttributes()
	 */
	public boolean hasAttributes() {
		return node.hasAttributes();
	}

	/**
	 * @see org.w3c.dom.Node#hasChildNodes()
	 */
	public boolean hasChildNodes() {
		return node.hasChildNodes();
	}

	/**
	 * @see org.w3c.dom.Node#insertBefore(Node, Node)
	 */
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return node.insertBefore(newChild, refChild);
	}

	/**
	 * @see org.w3c.dom.Node#isDefaultNamespace(String)
	 */
	public boolean isDefaultNamespace(String namespaceURI) {
		return node.isDefaultNamespace(namespaceURI);
	}

	/**
	 * @see org.w3c.dom.Node#isEqualNode(Node)
	 */
	public boolean isEqualNode(Node arg) {
		return node.isEqualNode(arg);
	}

	/**
	 * @see org.w3c.dom.Node#isSameNode(Node)
	 */
	public boolean isSameNode(Node other) {
		return node.isSameNode(other);
	}

	/**
	 * @see org.w3c.dom.Node#isSupported(String, String)
	 */
	public boolean isSupported(String feature, String version) {
		return node.isSupported(feature, version);
	}

	/**
	 * @see org.w3c.dom.Node#lookupNamespaceURI(String)
	 */
	public String lookupNamespaceURI(String prefix) {
		return node.lookupNamespaceURI(prefix);
	}

	/**
	 * @see org.w3c.dom.Node#lookupPrefix(String)
	 */
	public String lookupPrefix(String namespaceURI) {
		return node.lookupPrefix(namespaceURI);
	}

	/**
	 * @see org.w3c.dom.Node#normalize()
	 */
	public void normalize() {
		node.normalize();
	}

	/**
	 * @see org.w3c.dom.Node#removeChild(Node)
	 */
	public Node removeChild(Node oldChild) throws DOMException {
		return node.removeChild(oldChild);
	}

	/**
	 * @see org.w3c.dom.Node#replaceChild(Node, Node)
	 */
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return node.replaceChild(newChild, oldChild);
	}

	/**
	 * @see org.w3c.dom.Node#setNodeValue(String)
	 */
	public void setNodeValue(String nodeValue) throws DOMException {
		node.setNodeValue(nodeValue);
	}

	/**
	 * @see org.w3c.dom.Node#setPrefix(String)
	 */
	public void setPrefix(String prefix) throws DOMException {
		node.setPrefix(prefix);
	}

	/**
	 * @see org.w3c.dom.Node#setTextContent(String)
	 */
	public void setTextContent(String textContent) throws DOMException {
		node.setTextContent(textContent);
	}

	/**
	 * @see org.w3c.dom.Node#setUserData(String, Object, UserDataHandler)
	 */
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return node.setUserData(key, data, handler);
	}
}
