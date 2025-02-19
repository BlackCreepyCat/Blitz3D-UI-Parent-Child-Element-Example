Graphics 800,600,32,2
SetBuffer BackBuffer()

Type GUIElement
    Field x, y, w, h            ; Position et taille
    Field parent.GUIElement     ; Référence au parent
    Field firstChild.GUIElement ; Premier enfant
    Field nextSibling.GUIElement ; Prochain enfant du même parent
    Field isDragging            ; Booléen pour le déplacement
    Field offsetX, offsetY      ; Décalage souris lors du drag
    Field elementType           ; 1=Fenêtre, 2=Bouton...
    
    Field prevX
    Field prevY
    
    Field initialRelativeX      ; Position relative initiale par rapport au parent
    Field initialRelativeY
End Type

; Créer un nouvel élément GUI
Function CreateGUIElement.GUIElement(x, y, w, h, elementType, parent.GUIElement )
    gui.GUIElement = New GUIElement
    gui\x = x
    gui\y = y
    gui\w = w
    gui\h = h
    gui\elementType = elementType
    gui\parent = parent
    
    ; Si le parent est défini, ajuster la position de l'enfant par rapport à son parent
    If parent <> Null Then
        gui\x = parent\x + x
        gui\y = parent\y + y
        gui\initialRelativeX = x  ; Enregistrer la position relative initiale
        gui\initialRelativeY = y
        AddChild(parent, gui)
    EndIf
    
    Return gui
End Function

; Ajouter un enfant à un élément GUI
Function AddChild(parent.GUIElement, child.GUIElement)
    If parent\firstChild = Null Then
        parent\firstChild = child
    Else
        lastChild.GUIElement = parent\firstChild
        While lastChild\nextSibling <> Null
            lastChild = lastChild\nextSibling
        Wend
        lastChild\nextSibling = child
    EndIf
End Function


; Dessiner tous les éléments
Function DrawGUI()
    For gui.GUIElement = Each GUIElement
        If gui\parent = Null Then ; Ne dessiner que les parents
            DrawGUIElement(gui)
        EndIf
    Next
End Function

; Dessiner un élément et ses enfants
Function DrawGUIElement(gui.GUIElement)
    If gui\elementType = 1 ; Fenêtre
        Color 80, 80, 80 
		Rect gui\x, gui\y, gui\w, gui\h, 1
		
        Color 200, 200, 200 : Rect gui\x, gui\y, gui\w, 20, 1 ; Barre de titre
    ElseIf gui\elementType = 2 ; Bouton
        Color 120, 120, 250 : Rect gui\x, gui\y, gui\w, gui\h, 1
    EndIf
	
    ; Dessiner les enfants
    child.GUIElement = gui\firstChild
    While child <> Null
        DrawGUIElement(child)
        child = child\nextSibling
    Wend
End Function

; Mettre à jour la position des enfants lors du déplacement de la fenêtre
Function UpdateChildrenPosition(parent.GUIElement)
    child.GUIElement = parent\firstChild
    While child <> Null
        ; Calculer la position de l'enfant en fonction de la position relative initiale
        child\x = parent\x + child\initialRelativeX
        child\y = parent\y + child\initialRelativeY
        
        ; Si l'enfant a des enfants, les mettre à jour aussi
        UpdateChildrenPosition(child)
        
        child = child\nextSibling
    Wend
End Function


; Gestion des interactions (drag pour les fenêtres)
Function UpdateGUI()
    mx = MouseX() : my = MouseY()
    
    If MouseDown(1) ; Clic gauche
        For gui.GUIElement = Each GUIElement
            ; Sélection de la fenêtre si clic sur la barre de titre
            If gui\elementType = 1 And my >= gui\y And my <= gui\y + 20 And mx >= gui\x And mx <= gui\x + gui\w Then
                gui\isDragging = True
                gui\offsetX = mx - gui\x
                gui\offsetY = my - gui\y
                Exit ; Une seule fenêtre à la fois
            EndIf
        Next
    Else ; Relâchement du clic
        For gui.GUIElement = Each GUIElement
            gui\isDragging = False
        Next
    EndIf
    
    ; Déplacement des fenêtres
    For gui.GUIElement = Each GUIElement
        If gui\isDragging Then
            ; Sauvegarder la position précédente
            gui\prevX = gui\x
            gui\prevY = gui\y
            gui\x = mx - gui\offsetX
            gui\y = my - gui\offsetY
            ; Mettre à jour la position des enfants après le déplacement de la fenêtre
            UpdateChildrenPosition(gui)
        EndIf
    Next
End Function


; Création d'une fenêtre parent
window.GUIElement = CreateGUIElement(100, 100, 200, 150, 1, Null)

; Création d'un bouton enfant relatif à la fenêtre
button.GUIElement = CreateGUIElement(10, 10, 60, 30, 2, window)

; Création d'un bouton enfant
buttonB.GUIElement = CreateGUIElement(10, 10, 60, 30, 2, button)


; Création d'un bouton enfant
buttonC.GUIElement = CreateGUIElement(10, 10, 60, 30, 2, buttonB)


; Boucle principale
While Not KeyDown(1)
    Cls
	
    UpdateGUI()
    DrawGUI()
	
    Flip
Wend

End
;~IDEal Editor Parameters:
;~C#Blitz3D